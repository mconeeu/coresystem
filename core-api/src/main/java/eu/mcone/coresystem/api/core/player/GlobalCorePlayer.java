/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.translation.Language;

import java.util.Set;
import java.util.UUID;

public interface GlobalCorePlayer {

    /**
     * get players name
     * @return name
     */
    String getName();

    /**
     * get players uuid
     * @return uuid
     */
    UUID getUuid();

    /**
     * get players language
     * @return language
     */
    Language getLanguage();

    /**
     * get players group
     * @return group
     */
    Set<Group> getGroups();

    /**
     * get payers onlinetime in seconds
     * @return onlinetime long in seconds
     */
    long getOnlinetime();

    /**
     * get players permissions
     * @return permission set
     */
    Set<String> getPermissions();

    /**
     * check if player has a specific permissions
     * @param permission permission
     * @return boolean has permission
     */
    boolean hasPermission(String permission);

    /**
     * reload permissions from PermissionManager
     */
    void reloadPermissions();

    /**
     * returns the players main group
     * @return main group
     */
    Group getMainGroup();

    /**
     * set the players groups
     * @param groups group set
     */
    void setGroups(Set<Group> groups);

    /**
     * add a player group
     * @param group groups
     */
    void addGroup(Group group);

    /**
     * remove a player group
     * @param group group
     */
    void removeGroup(Group group);

    /**
     * check if a player is nicked
     * @return boolean nicked
     */
    boolean isNicked();

    /**
     * sends the player a message
     * @param message message
     */
    void sendMessage(String message);

    /**
     * unregisters a player from internal storage
     */
    void unregister();
    
}
