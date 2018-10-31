/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.Set;
import java.util.UUID;

public interface GlobalOfflineCorePlayer {

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
     * returns the amount of coins that the player has
     * @return players coins amount
     */
    int getCoins();

    /**
     * sets the given coin amount
     * @param coins amount
     */
    void setCoins(int coins);

    /**
     * adds the given amount to the players coins
     * @param amount amount
     */
    void addCoins(int amount);

    /**
     * removes the given amount from the players coins
     * if wished amount subtracted from his current is smaller than 0 it will be set to 0
     * @param amount amount
     */
    void removeCoins(int amount);


    Set<Group> updateGroupsFromDatabase();

    /**
     * returns the players main group
     * @return main group
     */
    Group getMainGroup();

    /**
     * returns the mcone player settings
     * @return player settings
     */
    PlayerSettings getSettings();

    /**
     * returns the current PlayerState
     * @return state
     */
    PlayerState getState();

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
     * returns the Discord UID from the players linked Discord identity
     * if no Discord identity is linked null will be returned
     * @return TeamSpeak UID
     */
    String getDiscordUid();

    /**
     * returns whether the player has a linked Discord ID
     * @return player has linked TS ID in database
     */
    boolean isDiscordIdLinked();

}
