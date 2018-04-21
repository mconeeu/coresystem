/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.Set;
import java.util.UUID;

public interface PermissionManager {
    
    void reload();
    
    Set<Group> getParents(Group group);
    
    Set<Group> getChildren(Group group);
    
    Set<String> getPermissions(String uuid, Set<Group> groups);
    
    boolean hasPermission(Set<String> permissions, String permission);

    Set<Group> getGroups();

    Set<Group> getGroups(String json);
    
    String getJson(Set<Group> groups);
    
    Group getLiveGroup(UUID uuid);
    
}
