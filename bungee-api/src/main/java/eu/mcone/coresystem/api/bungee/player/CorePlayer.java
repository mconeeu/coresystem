/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.Nick;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface CorePlayer extends GlobalCorePlayer, OfflineCorePlayer {

    Nick getCurrentNick();

    ProxiedPlayer bungee();

    boolean isNew();

}
