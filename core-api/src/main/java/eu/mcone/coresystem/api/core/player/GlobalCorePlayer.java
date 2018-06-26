/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import net.labymod.serverapi.LabyModConnection;

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
     * returns the unique id and other LabyMod spefific data if player is connected via LabyMod
     * @return LabyModConnection object
     */
    LabyModConnection getLabyModConnection();

    /**
     * returns the mcone player settings
     * @return player settings
     */
    PlayerSettings getSettings();

    /**
     * changes the player settings and updates both bungee & bukkit server
     * @param settings player settings
     */
    void updateSettings(PlayerSettings settings);

    /**
     * returns the TeamSpeak UID from the players linked TeamSpeak identity
     * if no TS identity is linked null will be returned
     * @return TeamSpeak UID
     */
    String getTeamspeakUid();

    /**
     * returns whether the player has a linked TS ID
     * @return player has linked TS ID in database
     */
    boolean isTeamspeakIdLinked();

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
