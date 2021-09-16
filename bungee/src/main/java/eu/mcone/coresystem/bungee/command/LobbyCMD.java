/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class LobbyCMD extends CorePlayerCommand {

    public LobbyCMD() {
        super("lobby", null, "l", "hub", "h");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        for (Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
            if (s.getKey().contains("Lobby")) {
                Msg.send(p, "§7Du wirst zur §fLobby §7gesendet...");
                p.connect(s.getValue());
                return;
            }
        }
    }

}
