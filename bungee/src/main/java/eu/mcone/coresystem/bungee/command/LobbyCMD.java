/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class LobbyCMD extends Command {

    public LobbyCMD() {
        super("lobby", null, "l", "hub", "h");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            for (Map.Entry<String, ServerInfo> s : ProxyServer.getInstance().getServers().entrySet()) {
                if (s.getKey().contains("Lobby")) {
                    CoreSystem.getInstance().getMessenger().send(sender, "§7Du wirst zur §fLobby §7gesendet...");
                    ((ProxiedPlayer) sender).connect(s.getValue());
                    return;
                }
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }
}
