/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TeamChatCMD extends Command {

    public TeamChatCMD() {
        super("teamchat", "system.bungee.teamchat", "tc");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return;

            if (args.length == 0) {
                BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/tc <Nachricht>");
            } else {
                StringBuilder message = new StringBuilder();
                for (String arg : args) {
                    message.append(arg).append(" ");
                }
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.hasPermission("system.bungee.teamchat")) {
                        BungeeCoreSystem.getInstance().getMessenger().sendSimple(
                                player,
                                BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.teamchat")
                                        .replaceAll(
                                                "%Playername%",
                                                BungeeCoreSystem.getInstance().getCorePlayer(p).getMainGroup().getPrefix() + p.getDisplayName()
                                        ) + message.toString()
                        );
                    }
                }
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }
}
