/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.core.mysql.MySQL;

import java.sql.SQLException;
import java.util.*;

public class PermissionManager implements eu.mcone.coresystem.api.core.player.PermissionManager {

    private final String servername;
    private final GlobalCoreSystem instance;

    private HashMap<Group, Set<String>> groups;
    private HashMap<Group, Set<Group>> parents;
    private HashMap<String, Set<String>> permissions;

    public PermissionManager(String servername, MySQL mysql, GlobalCoreSystem instance) {
        this.servername = servername != null ? servername : "unknownserver";
        this.instance = instance;

        this.groups = new HashMap<>();
        this.parents = new HashMap<>();
        this.permissions = new HashMap<>();

        this.reload();
    }

    public void reload() {
        instance.getMySQL(1).selectAsync("SELECT * FROM `bungeesystem_permissions` WHERE (`server` IS NULL OR `server` LIKE '" + servername.toLowerCase() + "')", rs -> {
            groups.clear();
            parents.clear();
            permissions.clear();

            try {
                while (rs.next()) {
                    switch (rs.getString("key")) {
                        case "group":
                            groups.put(Group.getGroupById(rs.getInt("name")), groups.getOrDefault(Group.getGroupById(rs.getInt("name")), new HashSet<>()));
                        case "permission":
                            addPermission(Group.getGroupById(rs.getInt("name")), rs.getString("value"));
                            break;
                        case "parent":
                            addParent(Group.getGroupById(rs.getInt("name")), Group.getGroupById(rs.getInt("value")));
                            break;
                        case "eu.mcone.coresystem.api.core.player-permission":
                            addPermissiontoPlayer(rs.getString("name"), rs.getString("value"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Set<Group> getParents(Group group) {
        Set<Group> result = new HashSet<>();
        for (Group parent : this.parents.getOrDefault(group, new HashSet<>())) {
            result.add(parent);
            result.addAll(getParents(parent));
        }
        return result;
    }

    public Set<Group> getChildren(Group group) {
        Set<Group> result = new HashSet<>();
        for (HashMap.Entry<Group, Set<Group>> entry : this.parents.entrySet()) {
            if (entry.getValue().contains(group)) {
                result.add(entry.getKey());
                result.addAll(getChildren(entry.getKey()));
            }
        }
        return result;
    }

    public Set<String> getPermissions(String uuid, Set<Group> groups) {
        if (groups.size() == 0) groups.add(Group.SPIELER);
        Set<String> permissions = new HashSet<>(this.permissions.getOrDefault(uuid, new HashSet<>()));

        for (Group g : groups) {
            permissions.add("group." + g.getName());
            permissions.addAll(this.groups.getOrDefault(g, new HashSet<>()));
            for (Group parent : getParents(g)) {
                permissions.addAll(this.groups.get(parent));
            }
        }

        return permissions;
    }

    public boolean hasPermission(Set<String> permissions, String permission) {
        if(permissions.contains(permission) || permissions.contains("*") || permission == null) {
            return true;
        } else {
            String[] permissionSplit = permission.replace('.', '-').split("-");
            StringBuilder permConstrutor = new StringBuilder();
            for(int i=0;i<permissionSplit.length-1;i++) {
                permConstrutor.append(permConstrutor.toString().equals("") ? "" : ".").append(permissionSplit[i]);
                if(permissions.contains(permConstrutor+".*")) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Group> getGroups() {
        return groups.keySet();
    }

    public Set<String> getGroupPermissions(Group group) {
        return groups.getOrDefault(group, null);
    }

    public Set<Group> getGroups(String json) {
        Set<Group> groups = new HashSet<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();

        for (JsonElement e : array) {
            groups.add(Group.getGroupById(e.getAsInt()));
        }

        return groups;
    }

    public String getJson(Set<Group> groups) {
        JsonArray array = new JsonArray();
        for (Group group : groups) array.add(group.getId());

        return new Gson().toJson(array);
    }

    public Group getLiveGroup(UUID uuid) {
        return (Group) instance.getMySQL(1).select("SELECT gruppe FROM userinfo WHERE uuid='"+uuid+"'", rs -> {
            try {
                if (rs.next()) return Group.getGroupbyName(rs.getString("gruppe"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private void addPermission(Group group, String permission) {
        if (this.groups.containsKey(group)) {
            this.groups.get(group).add(permission);
        } else {
            this.groups.put(group, new HashSet<>(Collections.singletonList(permission)));
        }
    }

    private void addPermissiontoPlayer(String uuid, String permission) {
        if (permissions.containsKey(uuid)) {
            permissions.get(uuid).add(permission);
        } else {
            permissions.put(uuid, new HashSet<>(Collections.singletonList(permission)));
        }
    }

    private void addParent(Group group, Group parent) {
        if (this.parents.containsKey(group)) {
            this.parents.get(group).add(parent);
        } else {
            this.parents.put(group, new HashSet<>(Collections.singletonList(parent)));
        }
    }

}
