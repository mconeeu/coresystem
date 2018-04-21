/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class LabyModAPI extends eu.mcone.coresystem.core.labymod.LabyModAPI {

    @Override
    public void send(GlobalCorePlayer player, byte[] bytes) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player.getUuid());

        if (p != null) {
            p.unsafe().sendPacket(new PluginMessage("LMC", bytes, false));
        }
    }

}
