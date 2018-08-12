/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionManager {

    /**
     * reload all permissions and groups from database (ATTENTION: CorePlayer object permission storage not included)
     */
    void reload();

    /**
     * get all parent groups from group, empty if group has no parents
     * @param group group
     * @return parent set
     */
    Set<Group> getParents(Group group);

    /**
     * get all child groups from gruop, empty if group has no children
     * @param group group
     * @return children set
     */
    Set<Group> getChildren(Group group);

    /**
     * get all permissions for a specific set of group and player
     * @param uuid player uuid
     * @param groups groups
     * @return permission set (including player-permissions)
     */
    Set<String> getPermissions(String uuid, Set<Group> groups);

    /**
     * check if permission set contains specific permission
     * @param permissions permission set
     * @param permission permission
     * @return boolean contains
     */
    boolean hasPermission(Set<String> permissions, String permission);

    /**
     * get all groups, in database
     * @return groups
     */
    Set<Group> getGroups();

    /**
     * get all groups from json String
     * @param groups List of group-ids
     * @return group set
     */
    Set<Group> getGroups(List<Integer> groups);

    /**
     * get json String from group set
     * @param groups group set
     * @return json String
     */
    String getJson(Set<Group> groups);

    /**
     * get live groups from database for specific player
     * @param uuid player uuid
     * @return group set
     */
    Set<Group> getLiveGroups(UUID uuid);
    
}
