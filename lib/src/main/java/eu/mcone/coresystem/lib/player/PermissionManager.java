/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.lib.player;

import eu.mcone.coresystem.lib.mysql.MySQL;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

public class PermissionManager {

    private String servername;
    private MySQL mysql;
    private HashMap<Group, List<String>> groups;
    private HashMap<Group, List<Group>> parents;
    private HashMap<String, List<String>> permissions;

    public PermissionManager(String servername, MySQL mysql) {
        this.servername = servername != null ? servername : "unknownserver";
        this.mysql = mysql;

        this.groups = new HashMap<>();
        this.parents = new HashMap<>();
        this.permissions = new HashMap<>();

        this.reload();
    }

    public void reload() {
        mysql.selectAsync("SELECT * FROM `bungeesystem_permissions` WHERE (`server` IS NULL OR `server` LIKE '" + servername.toLowerCase() + "')", rs -> {
            groups.clear();
            parents.clear();
            permissions.clear();

            try {
                while (rs.next()) {
                    switch (rs.getString("key")) {
                        case "group":
                            groups.put(Group.getGroupbyName(rs.getString("name")), groups.getOrDefault(Group.getGroupbyName(rs.getString("name")), new ArrayList<>()));
                        case "permission":
                            addPermission(Group.getGroupbyName(rs.getString("name")), rs.getString("value"));
                            break;
                        case "parent":
                            addParent(Group.getGroupbyName(rs.getString("name")), Group.getGroupbyName(rs.getString("value")));
                            break;
                        case "player-permission":
                            addPermissiontoPlayer(rs.getString("name"), rs.getString("value"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private List<Group> getParents(Group group) {
        List<Group> result = new ArrayList<>();
        for (Group parent : this.parents.getOrDefault(group, new ArrayList<>())) {
            result.add(parent);
            result.addAll(getParents(parent));
        }
        return result;
    }

    public List<Group> getChildren(Group group) {
        List<Group> result = new ArrayList<>();
        for (HashMap.Entry<Group, List<Group>> entry : this.parents.entrySet()) {
            if (entry.getValue().contains(group)) {
                result.add(entry.getKey());
                result.addAll(getChildren(entry.getKey()));
            }
        }
        return result;
    }

    public List<String> getPermissions(String uuid, Group group) {
        group = group != null ? group : Group.SPIELER;
        List<String> permissions = new ArrayList<>();

        permissions.add("group."+group.getName());
        permissions.addAll(this.groups.getOrDefault(group, new ArrayList<>()));
        permissions.addAll(this.permissions.getOrDefault(uuid, new ArrayList<>()));
        for (Group parent : getParents(group)) {
            permissions.addAll(this.groups.get(parent));
        }

        return permissions;
    }

    public boolean hasPermission(List<String> permissions, String permission) {
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

    public List<String> getGroupPermissions(Group group) {
        return groups.getOrDefault(group, null);
    }

    public Group getLiveGroup(UUID uuid) {
        return (Group) mysql.select("SELECT gruppe FROM userinfo WHERE uuid='"+uuid+"'", rs -> {
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
            this.groups.put(group, new ArrayList<>(Collections.singletonList(permission)));
        }
    }

    private void addPermissiontoPlayer(String uuid, String permission) {
        if (permissions.containsKey(uuid)) {
            permissions.get(uuid).add(permission);
        } else {
            permissions.put(uuid, new ArrayList<>(Collections.singletonList(permission)));
        }
    }

    private void addParent(Group group, Group parent) {
        if (this.parents.containsKey(group)) {
            this.parents.get(group).add(parent);
        } else {
            this.parents.put(group, new ArrayList<>(Collections.singletonList(parent)));
        }
    }

}
