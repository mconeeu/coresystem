/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerCMD extends CorePlayerCommand implements TabExecutor {

    public ServerCMD() {
        super("server", "system.bungee.server", "s");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 0) {
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§7Du befindest dich gerade auf dem Server: §f" + p.getServer().getInfo().getName());
            Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();

            ComponentBuilder cb = new ComponentBuilder(ChatColor.GRAY + "Du kannst dich jetzt mit folgenden Servern verbinden: ");
            int i = 0;
            for (ServerInfo si : servers.values()) {
                cb
                        .append(ChatColor.GREEN + si.getName())
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f" + si.getPlayers().size() + " Spieler\n§7§oLinksklick zum Joinen").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + si.getName()));
                i++;
                if (i >= servers.values().size()) break;

                cb.append(TextComponent.fromLegacyText("§7, "));
            }

            p.sendMessage(cb.create());
        } else if (args.length == 1) {
            final String serverName = args[0];
            final ServerInfo si = ProxyServer.getInstance().getServerInfo(serverName);

            if (si != null) {
                p.connect(si);
            } else {
                BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Dieser Server existiert nicht!");
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            String search = args[0];
            Set<String> matches = new HashSet<>();

            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                if (server.getName().startsWith(search)) {
                    matches.add(server.getName());
                }
            }

            return matches;
        }

        return ImmutableSet.of();
    }

}
