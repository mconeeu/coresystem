/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.core.mysql.MySQL;

import java.sql.SQLException;
import java.util.*;

public class PermissionManager implements eu.mcone.coresystem.api.core.player.PermissionManager {

    private final String servername;
    private MySQL mySQL;

    private HashMap<Group, Set<String>> groups;
    private HashMap<Group, Set<Group>> parents;
    private HashMap<String, Set<String>> permissions;

    public PermissionManager(String servername, MySQL mysql) {
        this.servername = servername != null ? servername : "unknownserver";
        this.mySQL = mysql;

        this.groups = new HashMap<>();
        this.parents = new HashMap<>();
        this.permissions = new HashMap<>();

        this.reload();
    }

    @Override
    public void reload() {
        mySQL.select("SELECT * FROM `permissions` WHERE (`server` IS NULL OR `server` LIKE '" + servername.toLowerCase() + "')", rs -> {
            groups.clear();
            parents.clear();
            permissions.clear();

            try {
                while (rs.next()) {
                    switch (rs.getString("key")) {
                        case "group":
                            groups.put(Group.getGroupById(rs.getInt("name")), groups.getOrDefault(Group.getGroupById(rs.getInt("name")), new HashSet<>()));
                            break;
                        case "permission":
                            addPermission(Group.getGroupById(rs.getInt("name")), rs.getString("value"));
                            break;
                        case "parent":
                            addParent(Group.getGroupById(rs.getInt("name")), Group.getGroupById(rs.getInt("value")));
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

    @Override
    public Set<Group> getParents(Group group) {
        Set<Group> result = new HashSet<>();
        for (Group parent : this.parents.getOrDefault(group, new HashSet<>())) {
            result.add(parent);
            result.addAll(getParents(parent));
        }
        return result;
    }

    @Override
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

    @Override
    public Set<String> getPermissions(String uuid, Set<Group> groups) {
        if (groups.size() == 0) groups.add(Group.SPIELER);
        Set<String> permissions = new HashSet<>(this.permissions.getOrDefault(uuid, Collections.emptySet()));

        for (Group g : groups) {
            permissions.add("group." + g.getName());
            permissions.addAll(this.groups.getOrDefault(g, Collections.emptySet()));
            for (Group parent : getParents(g)) {
                permissions.addAll(this.groups.get(parent));
            }
        }

        return permissions;
    }

    @Override
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

    @Override
    public Set<Group> getGroups() {
        return groups.keySet();
    }

    public Set<String> getGroupPermissions(Group group) {
        return groups.getOrDefault(group, null);
    }

    @Override
    public Set<Group> getGroups(List<Integer> groups) {
        Set<Group> result = new HashSet<>();

        for (Integer id : groups) {
            result.add(Group.getGroupById(id));
        }

        return result;
    }

    @Override
    public List<Integer> getGroupIDs(Set<Group> groups) {
        List<Integer> result = new ArrayList<>();

        for (Group g : groups) {
            result.add(g.getId());
        }

        return result;
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
