/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.util;

import org.bukkit.entity.Player;

public interface CoreActionBar {

    /**
     * set message
     * @param message message
     * @return this
     */
    CoreActionBar message(String message);

    /**
     * reset all values
     * @return this
     */
    CoreActionBar reset();

    /**
     * send to player
     * @param player player
     * @return this
     */
    CoreActionBar send(Player player);

}
