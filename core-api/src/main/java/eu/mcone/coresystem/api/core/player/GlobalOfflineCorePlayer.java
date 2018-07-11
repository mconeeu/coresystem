/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.Set;
import java.util.UUID;

public interface GlobalOfflineCorePlayer {

    UUID getUuid();

    String getName();

    PlayerState getState();

    Set<Group> getGroups();

    long getOnlinetime();

    PlayerSettings getSettings();

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

}
