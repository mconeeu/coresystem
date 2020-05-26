/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface NickManager {

    void reload();

    void nick(ProxiedPlayer player);

    void unnick(ProxiedPlayer player);

    void destroy(ProxiedPlayer player);

}
