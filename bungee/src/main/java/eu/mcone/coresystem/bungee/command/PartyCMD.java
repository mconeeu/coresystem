/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.friend.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PartyCMD extends CorePlayerCommand implements TabExecutor {

    public PartyCMD() {
        super("party", null, "p");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, final String[] args) {
        if (p.getServer().getInfo().getName().contains("Citybuild") || p.getServer().getInfo().getName().contains("Build")) {
            StringBuilder sb = new StringBuilder("ps");
            for (String arg : args) {
                sb.append(" ").append(arg);
            }

            CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "CMD", sb.toString());
        } else {
            if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("msg")) {
                    final Party party = Party.getParty(p);

                    if (party != null) {
                        StringBuilder msg = new StringBuilder();

                        if (p.equals(party.getLeader())) {
                            msg.append("§e♔ ").append(p.getName()).append(" §8» §f");
                        } else {
                            msg.append(p.getName()).append(" §8» §f");
                        }

                        for (int i = 1; i < args.length; i++) {
                            msg.append(args[i]).append(" ");
                        }

                        for (ProxiedPlayer m : party.getMember()) {
                            Party.getMessenger().send(m, msg.toString());
                        }
                    } else {
                        Party.getMessenger().send(p, "§4Du bist in keiner Party!");
                    }
                    return;
                } else if (args.length == 2) {
                    final ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[1]);
                    final Party party = Party.getParty(p);

                    if (t != null) {
                        final CorePlayer tc = CoreSystem.getInstance().getCorePlayer(t);

                        switch (args[0]) {
                            case "invite": {
                                if (Party.getParty(t) == null) {
                                    if (tc.getSettings().getPartyInvites().equals(PlayerSettings.Sender.NOBODY)) {
                                        Party.getMessenger().send(p, "§4Dieser Spieler hat Party-Einladungen ausgeschaltet!");
                                    } else if (tc.getSettings().getPartyInvites().equals(PlayerSettings.Sender.FRIENDS) && !tc.getFriendData().getFriends().containsKey(p.getUniqueId())) {
                                        Party.getMessenger().send(p, "§4Dieser Spieler emfängt nur Party-Einladungen von Freunden!");
                                    } else {
                                        if (party != null) {
                                            if (party.getLeader().equals(p)) {
                                                Party.getMessenger().send(p, "§f" + t.getName() + " §2wird in die Party eingeladen!");
                                                party.invite(t);
                                            } else {
                                                Party.getMessenger().send(p, "§4Du bist kein Partyleader!");
                                            }
                                        } else {
                                            Party.getMessenger().send(p, "§7§oDu bist in keiner Party!");
                                            new Party(p, t);
                                        }
                                    }
                                } else {
                                    Party.getMessenger().send(p, "§4Dieser Spieler ist bereits in einer Party!");
                                }
                                return;
                            }
                            case "accept": {
                                if (!Party.isInParty(p)) {
                                    if (Party.parties.containsKey(args[1].toLowerCase())) {
                                        Party.parties.get(args[1].toLowerCase()).addPlayer(p);
                                    } else {
                                        Party.getMessenger().send(p, "§4Dieser Spieler hat keine Party!");
                                    }
                                } else {
                                    Party.getMessenger().send(p, "§4Du bist bereits in einer Party");
                                }
                                return;
                            }
                            case "promote": {
                                if (party != null) {
                                    if (party.getLeader().equals(p)) {
                                        if (p != t) {
                                            if (party.getMember().contains(p)) {
                                                Party.getMessenger().send(p, "§f" + t.getName() + " §2wird zum Partyleader promotet!");
                                                party.promotePlayer(t);
                                            } else {
                                                Party.getMessenger().send(p, "§4Dieser Spieler befindet sich nicht in deiner Party!");
                                            }
                                        } else {
                                            Party.getMessenger().send(p, "§4Du bist bereits der Partyleader!");
                                        }
                                    } else {
                                        Party.getMessenger().send(p, "§4Du bist kein Partyleader!");
                                    }
                                } else {
                                    Party.getMessenger().send(p, "§4Du bist in keiner Party!");
                                }
                                return;
                            }
                            case "kick": {
                                if (party != null) {
                                    if (party.getLeader().equals(p)) {
                                        if (party.getMember().contains(p)) {
                                            Party.getMessenger().send(p, "§f" + t.getName() + " §2wird aus der Party gekickt!");
                                            party.removePlayer(t);

                                            Party.getMessenger().send(t, "§c" + p.getName() + "§4 hat dich aus der Party gekickt");
                                        } else {
                                            Party.getMessenger().send(p, "§4Dieser Spieler befindet sich nicht in deiner Party!");
                                        }
                                    } else {
                                        Party.getMessenger().send(p, "§4Du bist kein Partyleader!");
                                    }
                                } else {
                                    Party.getMessenger().send(p, "§4Du bist in keiner Party!");
                                }
                                return;
                            }
                        }
                    } else {
                        Party.getMessenger().send(p, "§4Der Spieler §c" + args[1] + " §4ist nicht online!");
                        return;
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (!Party.isInParty(p)) {
                        new Party(p);
                    } else {
                        Party.getMessenger().send(p, "§4Du bist bereits in einer Party");
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("leave")) {
                    final Party party = Party.getParty(p);

                    if (party != null) {
                        party.removePlayer(p);
                        Party.getMessenger().send(p, "§2Du hast die Party verlassen!");
                    } else {
                        Party.getMessenger().send(p, "§4Du bist in keiner Party!");
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("list")) {
                    final Party party = Party.getParty(p);

                    if (party != null) {
                        List<ProxiedPlayer> member = party.getMember();

                        StringBuilder result = new StringBuilder();
                        result.append("§7Die Party hat ").append(member.size()).append(" Mitglieder:\n");

                        int i = 0;
                        for (ProxiedPlayer m : member) {
                            i++;

                            if (i == member.size()) {
                                if (m.equals(party.getLeader())) {
                                    result.append("§e♔ §o").append(m.getName());
                                } else {
                                    result.append("§f§o").append(m.getName());
                                }
                                continue;
                            }
                            if (m.equals(party.getLeader())) {
                                result.append("§e♔ §o").append(m.getName()).append("§7, ");
                            } else {
                                result.append("§f§o").append(m.getName()).append("§7, ");
                            }
                        }

                        Party.getMessenger().send(p, result.toString());
                    } else {
                        Party.getMessenger().send(p, "§4Du bist in keiner Party!");
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("delete")) {
                    final Party party = Party.getParty(p);

                    if (party != null) {
                        if (party.getLeader().equals(p)) {
                            party.delete(p);
                        } else {
                            Party.getMessenger().send(p, "§4Du bist kein Partyleader!");
                        }
                    } else {
                        Party.getMessenger().send(p, "§4Du bist in keiner Party!");
                    }
                    return;
                }
            }

            Party.getMessenger().send(p, "§4Bitte benutze: §c/party <create | invite | msg | kick | promote | delete | accept | leave> [<Player>]");
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 1) {
                String search = args[0];
                Set<String> matches = new HashSet<>();

                for (String arg : new String[]{"create", "invite", "msg", "kick", "promote", "accept", "delete", "leave"}) {
                    if (arg.startsWith(search)) {
                        matches.add(arg);
                    }
                }

                return matches;
            } else if (args.length == 2) {
                for (String arg : new String[]{"invite", "kick", "promote", "accept"}) {
                    if (args[0].equalsIgnoreCase(arg)) {
                        String search = args[1];
                        Set<String> matches = new HashSet<>();
                        ProxiedPlayer p = (ProxiedPlayer) sender;

                        if (args[0].equalsIgnoreCase("invite")) {
                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                if (player != p && player.getName().startsWith(search)) {
                                    matches.add(player.getName());
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("accept")) {
                            for (Party party : Party.parties.values()) {
                                if (party.getInvites().contains(p)) {
                                    for (Map.Entry<String, Party> entry : Party.parties.entrySet()) {
                                        if (entry.getValue().equals(party) && entry.getKey().startsWith(search)) {
                                            matches.add(entry.getKey());
                                        }
                                    }
                                }
                            }
                        } else {
                            Party party = Party.getParty(p);

                            if (party != null) {
                                for (ProxiedPlayer player : party.getMember()) {
                                    if (player.getName().startsWith(search)) {
                                        matches.add(player.getName());
                                    }
                                }
                            }
                        }

                        return matches;
                    }
                }
            }
        }

        return ImmutableSet.of();
    }
}
