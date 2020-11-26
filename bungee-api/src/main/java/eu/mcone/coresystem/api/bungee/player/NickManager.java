/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.player.Nick;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.Map;

public interface NickManager {

    void reload();

    void nick(ProxiedPlayer player);

    void unnick(ProxiedPlayer player);

    void refreshNicks(Server server);

    void serverSwitched(ProxiedPlayer p);

    void destroy(ProxiedPlayer player);

    Nick getNick(String nickname);

    ProxiedPlayer getNickedUser(Nick nick);

    Map<Nick, ProxiedPlayer> getPlayerNicks();
}
