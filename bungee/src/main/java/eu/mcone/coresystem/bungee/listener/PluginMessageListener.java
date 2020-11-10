/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.MoneyChangeEvent;
import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
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

public class PluginMessageListener implements Listener {

    @EventHandler
    public void on(PluginMessageEvent e) {
        try {
            if (e.getTag().equalsIgnoreCase("BungeeCord")) {
                final DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
                final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getReceiver().toString());
                final CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

                String mainChannel = in.readUTF();

                if (mainChannel.equalsIgnoreCase("MC_ONE_GET")) {
                    String uuid = in.readUTF();
                    String subch = in.readUTF();

                    List<String> out = new ArrayList<>();

                    if (subch.equalsIgnoreCase("FRIENDS")) {
                        StringBuilder result = new StringBuilder();
                        Map<UUID, String> friends = cp.getFriendData().getFriends();

                        for (Map.Entry<UUID, String> friend : friends.entrySet()) {
                            ProxiedPlayer f = ProxyServer.getInstance().getPlayer(friend.getKey());
                            String online = "§coffline";
                            if (f != null) online = "§aonline";

                            result.append(friend.getKey()).append(":").append(friend.getValue()).append(":").append(online).append(",");
                        }

                        out.add(result.toString());
                    } else if (subch.equalsIgnoreCase("PARTY")) {
                        StringBuilder result = new StringBuilder();
                        Party party = Party.getParty(p);

                        if (party != null) {
                            List<ProxiedPlayer> members = party.getMember();
                            for (ProxiedPlayer member : members) {
                                result.append(member.getName()).append(":").append(member.getServer().getInfo().getName());
                                if (member.equals(party.getLeader())) result.append(":leader");
                                result.append(",");
                            }

                            out.add(result.toString());
                        } else {
                            out.add("false");
                        }
                    } else if (subch.equalsIgnoreCase("SERVERS")) {
                        String modus = in.readUTF();

                        StringBuilder result = new StringBuilder();
                        for (ServerInfo s : ProxyServer.getInstance().getServers().values()) {
                            if (s.getName().toLowerCase().contains(modus.toLowerCase()) && s.canAccess(p)) {
                                result.append(s.getName()).append(":").append(s.getPlayers().size()).append(";");
                            }
                        }

                        out.add(result.toString());
                    }

                    CoreSystem.getInstance().getChannelHandler().createReturnRequest(p.getServer(), uuid, out.toArray(new String[0]));
                } else if (mainChannel.equals("MC_ONE_SET")) {
                    String subch = in.readUTF();

                    if (subch.equalsIgnoreCase("REPORT")) {
                        String action = in.readUTF();

                        if (action.equalsIgnoreCase("NEW")) {
                            BungeeCoreSystem.getSystem().getOverwatch().getReportManager().addReport(in.readUTF());
                        } else if (action.equalsIgnoreCase("UPDATE")) {
                            BungeeCoreSystem.getSystem().getOverwatch().getReportManager().updateReportData(in.readUTF());
                        } else if (action.equalsIgnoreCase("ACCEPT")) {
                            //TODO: Check if the player is null when he is fast disconnecting
                            BungeeCoreSystem.getSystem().getOverwatch().getReportManager().acceptReport(in.readUTF(), ProxyServer.getInstance().getPlayer(UUID.fromString(in.readUTF())));
                        } else if (action.equalsIgnoreCase("REMOVE")) {
                            BungeeCoreSystem.getSystem().getOverwatch().getReportManager().removeTeamMember(in.readUTF());
                        }
                    } else if (subch.equalsIgnoreCase("PUNISH")) {
                        String id = in.readUTF();
                        System.out.println("PUNISH");

                        if (BungeeCoreSystem.getSystem().getOverwatch().getReportManager().existsReport(id)) {
                            System.out.println("EXISTS");
                            Report report = BungeeCoreSystem.getSystem().getOverwatch().getReportManager().getReport(id);
                            BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().punishPlayer(report, UUID.fromString(in.readUTF()));
                        }
                    } else if (subch.equalsIgnoreCase("CMD")) {
                        String input = in.readUTF();

                        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, input);
                    } else if (subch.equalsIgnoreCase("CONNECT")) {
                        String target = in.readUTF();
                        ServerInfo si = ProxyServer.getInstance().getServerInfo(target);

                        if (si != null && si.canAccess(p)) {
                            p.connect(si);
                        }
                    } else if (subch.equalsIgnoreCase("REFRESH_NICKS")) {
                        BungeeCoreSystem.getInstance().getNickManager().refreshNicks(p.getServer());
                    } else if (subch.equalsIgnoreCase("PLAYER_SETTINGS")) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerSettingsChangeEvent(
                                CoreSystem.getInstance().getCorePlayer(p),
                                CoreSystem.getInstance().getGson().fromJson(in.readUTF(), PlayerSettings.class)
                        ));
                    } else if (subch.equalsIgnoreCase("MONEY_CHANGE")) {
                        Currency currency = Currency.valueOf(in.readUTF());
                        int amount = Integer.parseInt(in.readUTF());

                        switch (currency) {
                            case COINS:
                                ((GlobalOfflineCorePlayer) cp).setCoinsAmount(amount);
                                break;
                            case EMERALDS:
                                ((GlobalOfflineCorePlayer) cp).setEmeraldsAmount(amount);
                                break;
                        }
                        ProxyServer.getInstance().getPluginManager().callEvent(new MoneyChangeEvent(cp, currency));
                    }
                } else if (mainChannel.equalsIgnoreCase("MC_ONE_REPLAY")) {
                    String subch = in.readUTF();
                    Server server = p.getServer();

                    if (subch.equalsIgnoreCase("REGISTER")) {
                        BungeeCoreSystem.getSystem().getServerSessionHandler().registerReplayServer(in.readUTF(), server, in.readUTF());
                    } else if (subch.equalsIgnoreCase("UNREGISTER")) {
                        BungeeCoreSystem.getSystem().getServerSessionHandler().unRegisterServer(server);
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

