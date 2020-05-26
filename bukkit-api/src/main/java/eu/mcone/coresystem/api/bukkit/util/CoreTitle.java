/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import org.bukkit.entity.Player;

public interface CoreTitle {

    /**
     * set title
     *
     * @param title message
     * @return this
     */
    CoreTitle title(String title);

    /**
     * set sub title
     *
     * @param subTitle message
     * @return this
     */
    CoreTitle subTitle(String subTitle);

    /**
     * set fade in time
     *
     * @param fadeIn time int in seconds
     * @return this
     */
    CoreTitle fadeIn(int fadeIn);

    /**
     * set stay time
     *
     * @param stay time int in seconds
     * @return this
     */
    CoreTitle stay(int stay);

    /**
     * set fade out time
     *
     * @param fadeOut time int in seconds
     * @return this
     */
    CoreTitle fadeOut(int fadeOut);

    /**
     * reset all values
     *
     * @return this
     */
    CoreTitle reset();

    /**
     * send Title to player
     *
     * @param player player
     * @return this
     */
    CoreTitle send(Player player);

}
