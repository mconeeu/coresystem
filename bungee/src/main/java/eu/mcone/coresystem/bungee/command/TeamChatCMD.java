/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.api.bungee.facades.Transl;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeamChatCMD extends CorePlayerCommand {

    public TeamChatCMD() {
        super("teamchat", "system.bungee.teamchat", "tc");
    }

    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 0) {
            Msg.send(p, "§4Bitte benutze: §c/tc <Nachricht>");
        } else {
            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.hasPermission("system.bungee.teamchat")) {
                    Msg.sendSimple(
                            player,
                            Transl.get("system.prefix.teamchat", p)
                                    .replaceAll(
                                            "%Playername%",
                                            BungeeCoreSystem.getInstance().getCorePlayer(p).getMainGroup().getPrefix() + p.getDisplayName()
                                    ) + message.toString()
                    );
                }
            }
        }
    }

}
