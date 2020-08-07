/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.overwatch.trust.TrustedUser;

import java.util.Set;
import java.util.UUID;

public interface GlobalOfflineCorePlayer {

    /**
     * get players name
     *
     * @return name
     */
    String getName();

    /**
     * get players uuid
     *
     * @return uuid
     */
    UUID getUuid();

    /**
     * get players permissions
     *
     * @return permission set
     */
    Set<String> getPermissions();

    /**
     * check if player has a specific permissions
     *
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
     *
     * @return group
     */
    Set<Group> getGroups();

    /**
     * get payers onlinetime in seconds
     *
     * @return onlinetime long in seconds
     */
    long getOnlinetime();

    /**
     * returns the amount of coins that the player has
     *
     * @return players coins amount
     */
    int getCoins();

    /**
     * returns the coins formatted with points
     *
     * @return coins amount formatted with points (e.g. #.###.###)
     */
    String getFormattedCoins();

    /**
     * sets the given coin amount
     *
     * @param coins amount
     */
    void setCoins(int coins);

    /**
     * adds the given amount to the players coins
     *
     * @param amount amount
     */
    void addCoins(int amount);

    /**
     * removes the given amount from the players coins
     * if wished amount subtracted from his current is smaller than 0 it will be set to 0
     *
     * @param amount amount
     */
    void removeCoins(int amount);

    /**
     * returns the amount of emeralds that the player has
     *
     * @return players coins amount
     */
    int getEmeralds();

    /**
     * returns the emeralds formatted with points
     *
     * @return emeralds amount formatted with points (e.g. #.###.###)
     */
    String getFormattedEmeralds();

    /**
     * sets the given emerald amount
     *
     * @param coins amount
     */
    void setEmeralds(int coins);

    /**
     * adds the given amount to the players emeralds
     *
     * @param amount amount
     */
    void addEmeralds(int amount);

    /**
     * removes the given amount from the players emeralds
     * if wished amount subtracted from his current is smaller than 0 it will be set to 0
     *
     * @param amount amount
     */
    void removeEmeralds(int amount);

    /**
     * refresh all players groups from database
     *
     * @return actual groups of the player
     */
    Set<Group> updateGroupsFromDatabase();

    /**
     * returns the players main group
     *
     * @return main group
     */
    Group getMainGroup();

    /**
     * returns the mcone player settings
     *
     * @return player settings
     */
    PlayerSettings getSettings();

    /**
     * returns the current PlayerState
     *
     * @return state
     */
    PlayerState getState();

    /**
     * returns the TeamSpeak UID from the players linked TeamSpeak identity
     * if no TS identity is linked null will be returned
     *
     * @return TeamSpeak UID
     */
    String getTeamspeakUid();

    /**
     * returns whether the player has a linked TS ID
     *
     * @return player has linked TS ID in database
     */
    boolean isTeamspeakIdLinked();

    /**
     * returns the Discord UID from the players linked Discord identity
     * if no Discord identity is linked null will be returned
     *
     * @return TeamSpeak UID
     */
    String getDiscordUid();

    /**
     * returns whether the player has a linked Discord ID
     *
     * @return player has linked TS ID in database
     */
    boolean isDiscordIdLinked();

    /**
     * returns a the trusted user
     *
     * @return TrustedUser
     */
    TrustedUser getTrust();

    /**
     * updates the trust values
     */
    void updateTrust();

    void increaseCorrectReports();

    void increaseWrongReports();

    boolean hasLinkedOneGamingAccount();

}
