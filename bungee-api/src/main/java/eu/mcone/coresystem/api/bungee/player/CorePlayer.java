/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface CorePlayer extends GlobalCorePlayer, OfflineCorePlayer {

    SkinInfo getNickedSkin();

    ProxiedPlayer bungee();

    boolean isNew();

}
