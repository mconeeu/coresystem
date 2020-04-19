/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.player;

import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.player.Group;
import org.bson.Document;

import java.util.*;

public class PermissionManager implements eu.mcone.coresystem.api.core.player.PermissionManager {

    private final String servername;
    private final MongoDatabase database;

    private final Map<Group, Set<String>> groups;
    private final Map<Group, Set<Group>> parents;
    private final HashMap<UUID, Set<String>> permissions;

    public PermissionManager(String servername, MongoDatabase database) {
        this.servername = servername != null ? servername : "unknownserver";
        this.database = database;

        this.groups = new HashMap<>();
        this.parents = new HashMap<>();
        this.permissions = new HashMap<>();

        this.reload();
    }

    @Override
    public void reload() {
        for (Document entry : database.getCollection("permission_groups").find()) {
            Group g = Group.getGroupById(entry.getInteger("id"));

            Set<String> permissions = new HashSet<>();
            for (Map.Entry<String, Object> e : entry.get("permissions", new Document()).entrySet()) {
                if (e.getValue() == null || (e.getValue() instanceof String && ((String) e.getValue()).equalsIgnoreCase(servername))) {
                    permissions.add(e.getKey().replace('-', '.'));
                }
            }
            this.groups.put(g, permissions);

            Set<Group> parents = new HashSet<>();
            for (int id : entry.get("parents", new ArrayList<Integer>())) {
                parents.add(Group.getGroupById(id));
            }
            this.parents.put(g, parents);
        }

        for (Document entry : database.getCollection("permission_players").find()) {
            Set<String> permissions = new HashSet<>();
            for (Map.Entry<String, Object> e : entry.get("permissions", new Document()).entrySet()) {
                if (e.getValue() == null || (e.getValue() instanceof String && ((String) e.getValue()).equalsIgnoreCase(servername))) {
                    permissions.add(e.getKey().replace('-', '.'));
                }
            }
            this.permissions.put(UUID.fromString(entry.getString("uuid")), permissions);
        }
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
    public Set<String> getPermissions(UUID uuid, Set<Group> groups) {
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

}
