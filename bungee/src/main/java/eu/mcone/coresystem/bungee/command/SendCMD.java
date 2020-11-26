/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SendCMD extends CoreCommand implements TabExecutor {

    public SendCMD() {
        super("send", "system.bungee.send");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Bitte benutze §c/send <server|player|all|current> <target>");
        } else {
            final ServerInfo target = ProxyServer.getInstance().getServerInfo(args[1]);

            if (target == null) {
                BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Der Server §c"+args[1]+" §4existiert nicht!");
                return;
            }

            if (args[0].equalsIgnoreCase("all")) {
                for (final ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    this.summon(p, target, sender);
                }
            } else if (args[0].equalsIgnoreCase("current")) {
                if (!(sender instanceof ProxiedPlayer)) {
                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Nur ein Spieler kann diesen Befehl benutzen!");
                    return;
                }

                final ProxiedPlayer player = (ProxiedPlayer)sender;
                for (final ProxiedPlayer p2 : player.getServer().getInfo().getPlayers()) {
                    this.summon(p2, target, sender);
                }
            } else {
                final ServerInfo serverTarget = ProxyServer.getInstance().getServerInfo(args[0]);

                if (serverTarget != null) {
                    for (final ProxiedPlayer p2 : serverTarget.getPlayers()) {
                        this.summon(p2, target, sender);
                    }
                } else {
                    final ProxiedPlayer player2 = ProxyServer.getInstance().getPlayer(args[0]);
                    if (player2 == null) {
                        BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Der Spieler §c"+args[0]+" §4ist nicht online!");
                        return;
                    }
                    this.summon(player2, target, sender);
                }
            }

            BungeeCoreSystem.getInstance().getMessenger().send(sender, "§2Spieler wurden erfolgreich gesendet!");
        }
    }

    private void summon(final ProxiedPlayer player, final ServerInfo target, final CommandSender sender) {
        if (player.getServer() != null && !player.getServer().getInfo().equals(target)) {
            player.connect(target, ServerConnectEvent.Reason.COMMAND);
            BungeeCoreSystem.getInstance().getMessenger().send(player, "§7Du wurdest von §o"+sender.getName()+"§7 zum Server §f"+target.getName()+"§7 gesendet!");
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length > 2 || args.length == 0) {
            return Collections.emptySet();
        } else {
            final Set<String> matches = new HashSet<>();
            if (args.length == 1) {
                final String search = args[0].toLowerCase(Locale.ROOT);

                for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().toLowerCase(Locale.ROOT).startsWith(search)) {
                        matches.add(player.getName());
                    }
                }
                if ("all".startsWith(search)) {
                    matches.add("all");
                }
                if ("current".startsWith(search)) {
                    matches.add("current");
                }
            } else {
                final String search = args[1].toLowerCase(Locale.ROOT);

                for (final String server : ProxyServer.getInstance().getServers().keySet()) {
                    if (server.toLowerCase(Locale.ROOT).startsWith(search)) {
                        matches.add(server);
                    }
                }
            }

            return matches;
        }
    }

}
