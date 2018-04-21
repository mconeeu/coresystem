/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.Set;
import java.util.UUID;

public interface GlobalCorePlayer {
    
    String getName();
    
    UUID getUuid();
    
    Set<Group> getGroups();

    long getOnlinetime();
    
    Set<String> getPermissions();
    
    boolean hasPermission(String permission);
    
    void reloadPermissions();
    
    Group getMainGroup();
    
    void setGroups(Set<Group> groups);
    
    void addGroup(Group group);
    
    void removeGroup(Group group);

    boolean isNicked();

    void setNicked(boolean nicked);

    void sendMessage(String message);
    
    void unregister();
    
}
