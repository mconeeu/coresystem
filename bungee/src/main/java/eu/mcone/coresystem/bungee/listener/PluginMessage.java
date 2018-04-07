/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PluginMessage implements Listener {

    @EventHandler
    public void on(PluginMessageEvent e) {
        if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getReceiver().toString());
            final CorePlayer cp = CoreSystem.getCorePlayer(p);
            final ServerInfo server = p.getServer().getInfo();

            try {
                String ch = in.readUTF();
                if (ch.equalsIgnoreCase("mc1main")) {
                    String subch = in.readUTF();
                    if (subch.equalsIgnoreCase("CMD")) {
                        String input = in.readUTF();

                        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, input);
                    } else if (subch.equalsIgnoreCase("FRIENDS")) {
                        String input = in.readUTF();

                        if (input.equalsIgnoreCase("friends")) {
                            StringBuilder result = new StringBuilder();
                            Map<UUID, String> friends = cp.getFriends();

                            for (Map.Entry<UUID, String> friend : friends.entrySet()) {
                                ProxiedPlayer f = ProxyServer.getInstance().getPlayer(friend.getKey());
                                String online = "§coffline";
                                if (f != null) online = "§aonline";

                                result.append(friend.getKey()).append(":").append(friend.getValue()).append(":").append(online).append(",");
                            }

                            new eu.mcone.coresystem.bungee.utils.PluginMessage("Return", server, "FRIENDS", "friends", p.getUniqueId().toString(), result.toString());
                        }
                    } else if (subch.equalsIgnoreCase("PARTY")) {
                        String input = in.readUTF();

                        if (input.equalsIgnoreCase("member")) {
                            StringBuilder result = new StringBuilder();
                            Party party = Party.getParty(p);

                            if (party != null) {
                                List<ProxiedPlayer> members = party.getMember();
                                for (ProxiedPlayer member : members) {
                                    result.append(member.getName()).append(":").append(member.getServer().getInfo().getName());
                                    if (member.equals(party.getLeader())) result.append(":leader");
                                    result.append(",");
                                }

                                new eu.mcone.coresystem.bungee.utils.PluginMessage("Return", server, "PARTY", "member", p.getUniqueId().toString(), result.toString());
                            } else {
                                new eu.mcone.coresystem.bungee.utils.PluginMessage("Return", server, "PARTY", "member", p.getUniqueId().toString(), "false");
                            }
                        }
                    } else if (subch.equalsIgnoreCase("SERVERS")) {
                        String input = in.readUTF();

                        if (input.equalsIgnoreCase("list")) {
                            String modus = in.readUTF();

                            StringBuilder result = new StringBuilder();
                            for (ServerInfo s : ProxyServer.getInstance().getServers().values()) {
                                if (s.getName().toLowerCase().contains(modus.toLowerCase()) && s.canAccess(p)) {
                                    result.append(s.getName()).append(":").append(s.getPlayers().size()).append(";");
                                }
                            }

                            new eu.mcone.coresystem.bungee.utils.PluginMessage("Return", server, "SERVERS", "list", p.getUniqueId().toString(), result.toString());
                        }
                    } else if (subch.equalsIgnoreCase("CONNECT")) {
                        String target = in.readUTF();
                        ServerInfo si = ProxyServer.getInstance().getServerInfo(target);

                        if (si != null && si.canAccess(p)) {
                            p.connect(si);
                        }
                    } else if (subch.equalsIgnoreCase("UNNICK")) {
                        CoreSystem.getInstance().getNickManager().destroy(p);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
