/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartyCMD extends Command implements TabExecutor {

    public PartyCMD() {
        super("party", null, "p");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;

            if (p.getServer().getInfo().getName().contains("Citybuild") || p.getServer().getInfo().getName().contains("Build")) {
                StringBuilder sb = new StringBuilder("ps");
                for (String arg : args) {
                    sb.append(" ").append(arg);
                }

                CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "CMD", sb.toString());
            } else {
                if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

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
                                BungeeCoreSystem.getInstance().getMessenger().sendParty(m, msg.toString());
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist in keiner Party!");
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
                                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Dieser Spieler hat Party-Einladungen ausgeschaltet!");
                                        } else if (tc.getSettings().getPartyInvites().equals(PlayerSettings.Sender.FRIENDS) && !tc.getFriendData().getFriends().containsKey(p.getUniqueId())) {
                                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Dieser Spieler emfängt nur Party-Einladungen von Freunden!");
                                        } else {
                                            if (party != null) {
                                                if (party.getLeader().equals(p)) {
                                                    BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§f" + t.getName() + " §2wird in die Party eingeladen!");
                                                    party.invite(t);
                                                } else {
                                                    BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist kein Partyleader!");
                                                }
                                            } else {
                                                BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§7§oDu bist in keiner Party!");
                                                new Party(p, t);
                                            }
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Dieser Spieler ist bereits in einer Party!");
                                    }
                                    return;
                                }
                                case "accept": {
                                    if (!Party.isInParty(p)) {
                                        if (Party.parties.containsKey(args[1].toLowerCase())) {
                                            Party.parties.get(args[1].toLowerCase()).addPlayer(p);
                                        } else {
                                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Dieser Spieler hat keine Party!");
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist bereits in einer Party");
                                    }
                                    return;
                                }
                                case "promote": {
                                    if (party != null) {
                                        if (party.getLeader().equals(p)) {
                                            if (p != t) {
                                                if (party.getMember().contains(p)) {
                                                    BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§f" + t.getName() + " §2wird zum Partyleader promotet!");
                                                    party.promotePlayer(t);
                                                } else {
                                                    BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Dieser Spieler befindet sich nicht in deiner Party!");
                                                }
                                            } else {
                                                BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist bereits der Partyleader!");
                                            }
                                        } else {
                                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist kein Partyleader!");
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist in keiner Party!");
                                    }
                                    return;
                                }
                                case "kick": {
                                    if (party != null) {
                                        if (party.getLeader().equals(p)) {
                                            if (party.getMember().contains(p)) {
                                                BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§f" + t.getName() + " §2wird aus der Party gekickt!");
                                                party.removePlayer(t);

                                                BungeeCoreSystem.getInstance().getMessenger().sendParty(t, "§c"+p.getName()+"§4 hat dich aus der Party gekickt");
                                            } else {
                                                BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Dieser Spieler befindet sich nicht in deiner Party!");
                                            }
                                        } else {
                                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist kein Partyleader!");
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist in keiner Party!");
                                    }
                                    return;
                                }
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Der Spieler §c" + args[1] + " §4ist nicht online!");
                            return;
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (!Party.isInParty(p)) {
                            new Party(p);
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist bereits in einer Party");
                        }
                        return;
                    } else if (args[0].equalsIgnoreCase("leave")) {
                        final Party party = Party.getParty(p);

                        if (party != null) {
                            party.removePlayer(p);
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§2Du hast die Party verlassen!");
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist in keiner Party!");
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

                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, result.toString());
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist in keiner Party!");
                        }
                        return;
                    } else if (args[0].equalsIgnoreCase("delete")) {
                        final Party party = Party.getParty(p);

                        if (party != null) {
                            if (party.getLeader().equals(p)) {
                                party.delete(p);
                            } else {
                                BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist kein Partyleader!");
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Du bist in keiner Party!");
                        }
                        return;
                    }
                }

                BungeeCoreSystem.getInstance().getMessenger().sendParty(p, "§4Bitte benutze: §c/party <create | invite | msg | kick | promote | delete | leave> [<Player>]");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("create", "invite", "msg", "kick", "promote", "delete", "leave"));
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("msg")) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    result.add(p.getName());
                }
            }
        }

        return result;
    }
}
