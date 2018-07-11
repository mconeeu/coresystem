/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.player.SkinInfo;
import org.bukkit.entity.Player;

public interface NickManager {

    /**
     * nicks a player with a specific name and skin
     * @param player player
     * @param name nickname
     * @param skin SkinInfo object
     */
    void nick(Player player, String name, SkinInfo skin);

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
