/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import java.util.UUID;

public interface AfkManager {

    /**
     * returns true if player is currently afk
     * @param uuid the wished players uuid
     * @return player is afk
     */
    boolean isAfk(UUID uuid);

    /**
     * returns the players afk time in seconds as long
     * if player is not afk it will return 0
     * @param uuid the wished players uuid
     * @return players afk time long
     */
    long getAfkTime(UUID uuid);

    /**
     * starts the AfkManager
     * ATTENTION: this will throw an exception if the AfkManager was not disabled before!
     */
    void start();

    /**
     * disables the AfkManager
     */
    void disable();

}
