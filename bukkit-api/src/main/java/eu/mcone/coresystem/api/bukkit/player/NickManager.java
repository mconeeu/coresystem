/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.player.Nick;
import org.bukkit.entity.Player;

public interface NickManager {

    /**
     * nicks a player with a specific name and skin
     *  @param player Player
     * @param nick   nick
     * @param notify
     */
    void nick(Player player, Nick nick, boolean notify);

    /**
     * unnicks a player
     *  @param player     player
     *
     */
    void unnick(Player player);

    /**
     * if false the default /nick command will only change the name but not the skin
     *
     * @return /nick command is allowed to change skins
     */
    boolean isAllowSkinChange();

    /**
     * if false the default /nick command will only change the name but not the skin
     *
     * @param allowSkinChange allows to change skin on /nick
     */
    void setAllowSkinChange(boolean allowSkinChange);

}
