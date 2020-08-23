/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class FriendCMD extends Command implements TabExecutor {

    public FriendCMD() {
        super("friend", null, "f", "friends", "freund");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer bp = (ProxiedPlayer) sender;
            final CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(bp);

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUuid())) return;

            if (args.length == 1) {
                switch (args[0]) {
                    case "list": {
                        Map<UUID, String> friends = p.getFriendData().getFriends();
                        StringBuilder result = new StringBuilder();

                        if (friends.size() == 0) {
                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§7Du hast momentan keine Freunde!");
                            return;
                        } else if (friends.size() == 1) {
                            result.append("§7Du hast einen Freund:\n");
                        } else {
                            result.append("§7Du hast ").append(friends.size()).append(" Freunde:\n");
                        }

                        appendListToStringBuilder(result, friends.values());

                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, result.toString());
                        return;
                    }
                    case "req":
                    case "request":
                    case "requests": {
                        Map<UUID, String> requests = p.getFriendData().getRequests();
                        StringBuilder result = new StringBuilder();

                        if (p.getSettings().isEnableFriendRequests()) BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§7§oDu hast Freundschaftsanfragen deaktiviert! Aktiviere sie mit §f/friend requests toggle");

                        if (requests.size() == 0) {
                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§7Du hast momentan keine Freundschaftsanfragen!");
                            return;
                        } else if (requests.size() == 1) {
                            result.append("§7Du hast eine Freundschaftsanfrage von:\n");
                        } else {
                            result.append("§7Du hast ").append(requests.size()).append(" Freundschaftsanfragen von:\n");
                        }

                        appendListToStringBuilder(result, requests.values());

                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, result.toString());
                        return;
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("req") ||args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("requests")){
                    if (args[1].equalsIgnoreCase("toggle")) {
                        if (p.getSettings().isEnableFriendRequests()) {
                            p.getSettings().setEnableFriendRequests(false);
                            p.updateSettings();

                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast Freundschaftsanfragen §aausgeschaltet!");
                        } else {
                            p.getSettings().setEnableFriendRequests(true);
                            p.updateSettings();

                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast Freundschaftsanfragen §aeingeschaltet!");
                        }
                    }
                    return;
                } else if (!p.getName().equalsIgnoreCase(args[1])) {
                    String target = args[1];
                    try {
                        OfflineCorePlayer t = BungeeCoreSystem.getInstance().getOfflineCorePlayer(target);

                        switch (args[0]) {
                            case "add": {
                                if (!p.getFriendData().getFriends().containsKey(t.getUuid())) {
                                    if (p.getFriendData().getFriends().size() <= 44) {
                                        if (!t.getFriendData().getBlocks().contains(p.getUuid())) {
                                            if (p.getFriendData().getRequests().containsKey(t.getUuid())) {
                                                BungeeCoreSystem.getInstance().getFriendSystem().addFriend(p.getUuid(), t.getUuid(), target);
                                                BungeeCoreSystem.getInstance().getFriendSystem().addFriend(t.getUuid(), p.getUuid(), p.getName());
                                                BungeeCoreSystem.getInstance().getFriendSystem().removeRequest(p.getUuid(), t.getUuid());

                                                BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du bist nun mit §f" + target + " §2befreundet!");

                                                ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                                if (f != null) {
                                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(f, "§f" + p.getName() + "§2 hat deine Freundschaftanfrage angenommen!");
                                                }
                                            } else if (!t.getFriendData().getRequests().containsKey(p.getUuid())) {
                                                if (t.getSettings().isEnableFriendRequests()) {
                                                    BungeeCoreSystem.getInstance().getFriendSystem().addRequest(BungeeCoreSystem.getInstance().getPlayerUtils().fetchUuid(target), p.getUuid(), p.getName());
                                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast §f" + target + "§2 eine Freundschaftsanfrage geschickt!");

                                                    if (t.getFriendData().getFriends().size() > 44) {
                                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§c" + target + "§4 hat die maximale Anzahl an Freunden erreicht und kann gerade keine weiteren Freundschaftsanfragen annehmen! Die Anfrage wurde trotzdem versendet.");
                                                    }

                                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                                    if (f != null) {
                                                        f.sendMessage(
                                                                new ComponentBuilder("")
                                                                        .append(TextComponent.fromLegacyText(BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.friend")))
                                                                        .append(TextComponent.fromLegacyText("§f" + p.getName() + "§7 hat dir eine Freundschaftanfrage geschickt!\n"))
                                                                        .append(TextComponent.fromLegacyText(BungeeCoreSystem.getInstance().getTranslationManager().get("system.prefix.friend")))
                                                                        .append(TextComponent.fromLegacyText("§a[ANNEHMEN]"))
                                                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§o/friend add " + p.getName()).create()))
                                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend add " + p.getName()))
                                                                        .append(" ")
                                                                        .append(TextComponent.fromLegacyText("§c[ABLEHNEN]"))
                                                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§o/friend decline " + p.getName()).create()))
                                                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + p.getName()))
                                                                        .create()
                                                        );
                                                    }
                                                } else {
                                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§c" + target + "§4 hat Freundschaftsanfragen deaktiviert!");
                                                }
                                            } else {
                                                BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du hast §c" + target + "§4 bereits eine Freundschaftsanfrage geschickt!");
                                            }
                                        } else {
                                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§c" + target + "§4 hat dich blockiert!");
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du kannst nicht mehr als 44 Freunde gleichzeitig haben!");
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du bist bereits mit §c" + target + "§4 befreundet!");
                                }
                                return;
                            }
                            case "delete":
                            case "remove": {
                                if (p.getFriendData().getFriends().containsKey(t.getUuid())) {
                                    BungeeCoreSystem.getInstance().getFriendSystem().removeFriend(p.getUuid(), t.getUuid());
                                    BungeeCoreSystem.getInstance().getFriendSystem().removeFriend(t.getUuid(), p.getUuid());
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§f" + target + "§2 wurde aus deiner §2Freundesliste entfernt!");

                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                    if (f != null) {
                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(f, "§c" + p.getName() + "§4 hat dich aus seiner Freundesliste entfernt!");
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§c" + target + "§4 befindet sich nicht in deiner Freundeliste!");
                                }
                                return;
                            }
                            case "accept": {
                                if (p.getFriendData().getFriends().size() <= 44) {
                                    Map<UUID, String> reqests = p.getFriendData().getRequests();

                                    if (reqests.containsKey(t.getUuid())) {
                                        BungeeCoreSystem.getInstance().getFriendSystem().addFriend(p.getUuid(), t.getUuid(), target);
                                        BungeeCoreSystem.getInstance().getFriendSystem().addFriend(t.getUuid(), p.getUuid(), p.getName());
                                        BungeeCoreSystem.getInstance().getFriendSystem().removeRequest(p.getUuid(), t.getUuid());

                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast die Freundschaftsanfrage von §f" + target + " §2erfolgreich angenommen!");

                                        ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                        if (f != null) {
                                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(f, "§f" + p.getName() + "§2 hat deine Freundschaftanfrage angenommen!");
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§c" + target + " §4hat dir keine Freundschaftsanfrage geschickt!");
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du kannst nicht mehr als 44 Freunde gleichzeitig haben! Entferne einen Freund und versuche es erneut.");
                                }
                                return;
                            }
                            case "deny":
                            case "decline": {
                                Map<UUID, String> reqests = p.getFriendData().getRequests();

                                if (reqests.containsKey(t.getUuid())) {
                                    BungeeCoreSystem.getInstance().getFriendSystem().removeRequest(p.getUuid(), t.getUuid());
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast die Freundschaftsanfrage von §f" + target + " §2erfolgreich abgelehnt!");

                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                    if (f != null) {
                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(f, "§c" + p.getName() + "§4 hat deine Freundschaftanfrage abgelehnt!");
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§c" + target + " §4hat dir keine Freundschaftsanfrage geschickt!");
                                }
                                return;
                            }
                            case "block": {
                                if (!t.hasPermission("group.team")) {
                                    if (!p.getFriendData().getBlocks().contains(t.getUuid())) {
                                        BungeeCoreSystem.getInstance().getFriendSystem().addBlock(p.getUuid(), t.getUuid());
                                        BungeeCoreSystem.getInstance().getFriendSystem().removeFriend(p.getUuid(), t.getUuid());
                                        BungeeCoreSystem.getInstance().getFriendSystem().removeFriend(t.getUuid(), p.getUuid());

                                        ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                        if (f != null) {
                                            BungeeCoreSystem.getInstance().getMessenger().sendFriend(f, "§c" + p.getName() + "§4 hat dich aus seiner Freundesliste entfernt!");
                                        }
                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast §f" + target + "§2 erfolgreich blockiert!");
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du hast §c" + target + "§4 bereits blockiert!");
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du darft kein §cTeam-Mitglied §4blockieren!");
                                }
                                return;
                            }
                            case "unblock": {
                                if (p.getFriendData().getBlocks().contains(t.getUuid())) {
                                    BungeeCoreSystem.getInstance().getFriendSystem().removeBlock(p.getUuid(), t.getUuid());
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§2Du hast §f" + target + "§2 erfolgreich aus deinen blockierten Spielern gelöscht!");
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Du hast §c" + target + "§4 nicht blockiert!");
                                }
                                return;
                            }
                        }
                    } catch (CoreException e) {
                        BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Der Spielername §c" + args[0] + "§4 existiert nicht!");
                        return;
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Bitte wähle einen anderen Spieler als dich selbst!");
                    return;
                }
            }

            BungeeCoreSystem.getInstance().getMessenger().sendFriend(bp, "§4Bitte benutze: §c/friend <list | requests | accept | deny | add | remove | block | unblock> §c[<name>] §4oder §c/friend request toggle");
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("list", "requests", "accept", "deny", "add", "remove", "block", "unblock"));
        } else if (args.length == 2) {
            if (!(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("requests") || args[0].equalsIgnoreCase("req") || args[0].equalsIgnoreCase("request"))) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    result.add(p.getName());
                }
            }
        }

        return result;
    }

    private void appendListToStringBuilder(StringBuilder sb, Collection<String> list) {
        int i = 0;
        for (String friend : list) {
            i++;

            if (i == list.size()) {
                sb.append("§f§o").append(friend);
                continue;
            }
            sb.append("§f§o").append(friend).append("§7, ");
        }
    }

}
