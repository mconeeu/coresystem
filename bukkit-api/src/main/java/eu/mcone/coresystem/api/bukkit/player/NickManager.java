/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import org.bukkit.entity.Player;

public interface NickManager {

    /**
     * nicks a player with a specific name and skin
     * @param player player
     * @param name nickname
     * @param value skin mojang-value
     * @param signature skin mojang-signature
     */
    void nick(Player player, String name, String value, String signature);

    /**
     * unnicks a player
     * @param player player
     */
    void unnick(Player player);

}
