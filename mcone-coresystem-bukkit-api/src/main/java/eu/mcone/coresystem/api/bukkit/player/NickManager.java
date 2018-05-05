/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import org.bukkit.entity.Player;

public interface NickManager {

    void nick(Player p, String name, String value, String signature);

    void setNicks(Player p);

    void unnick(Player p);

}
