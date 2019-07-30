/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public interface CoreActionBar {

    /**
     * set message
     * @param message message
     * @return this
     */
    CoreActionBar message(BaseComponent[] message);

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
