/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BungeeCorePlayer extends GlobalCorePlayer {

    long getMuteTime();

    Map<UUID, String> getFriends();

    Map<UUID, String> getFriendRequests();

    List<UUID> getBlocks();

    boolean isRequestsToggled();

    SkinInfo getNickedSkin();

    ProxiedPlayer bungee();

    boolean isMuted();

}
