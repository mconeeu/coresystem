/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import org.bukkit.entity.Player;

public interface CoreTablistInfo {

    /**
     * set header
     * @param header message
     * @return this
     */
    CoreTablistInfo header(String header);

    /**
     * set footer
     * @param footer message
     * @return this
     */
    CoreTablistInfo footer(String footer);

    /**
     * reset all values
     * @return this
     */
    CoreTablistInfo reset();

    /**
     * send to player
     * @param player player
     * @return this
     */
    CoreTablistInfo send(Player player);

}
