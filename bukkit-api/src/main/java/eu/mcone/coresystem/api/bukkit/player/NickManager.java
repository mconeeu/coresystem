/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.player.Nick;
import org.bukkit.entity.Player;

public interface NickManager {

    /**
     *  nicks a player with a specific name and skin
     * @param player Player
     * @param nick nick
     */
    void nick(Player player, Nick nick);

    /**
     * nicks a player with a specific name and without skin
     * @param player player
     * @param name nickname
     */
    void nick(Player player, String name);

    /**
     * unnicks a player
     * @param player player
     * @param bypassSkin should the skin be bypassed by unnick?
     */
    void unnick(Player player, boolean bypassSkin);

    /**
     * if false the default /nick command will only change the name but not the skin
     * @return /nick command is allowed to change skins
     */
    boolean isAllowSkinChange();

    /**
     * if false the default /nick command will only change the name but not the skin
     * @param allowSkinChange allows to change skin on /nick
     */
    void setAllowSkinChange(boolean allowSkinChange);

}
