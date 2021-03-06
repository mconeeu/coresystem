/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.api.bungee.facades.Transl;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.friend.FriendSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class FriendCMD extends CorePlayerCommand implements TabExecutor {

    public FriendCMD() {
        super("friend", null, "f", "friends", "freund");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer bp, String[] args) {
        final CorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(bp);

        if (args.length == 1) {
            switch (args[0]) {
                case "list": {
                    Map<UUID, String> friends = p.getFriendData().getFriends();
                    StringBuilder result = new StringBuilder();

                    if (friends.size() == 0) {
                        FriendSystem.getMessenger().send(bp, "§7Du hast momentan keine Freunde!");
                        return;
                    } else if (friends.size() == 1) {
                        result.append("§7Du hast einen Freund:\n");
                    } else {
                        result.append("§7Du hast ").append(friends.size()).append(" Freunde:\n");
                    }

                    appendListToStringBuilder(result, friends.values());

                    FriendSystem.getMessenger().send(bp, result.toString());
                    return;
                }
                case "req":
                case "request":
                case "requests": {
                    Map<UUID, String> requests = p.getFriendData().getRequests();
                    StringBuilder result = new StringBuilder();

                    if (!p.getSettings().isEnableFriendRequests())
                        FriendSystem.getMessenger().send(bp, "§7§oDu hast Freundschaftsanfragen deaktiviert! Aktiviere sie mit §f/friend requests toggle");

                    if (requests.size() == 0) {
                        FriendSystem.getMessenger().send(bp, "§7Du hast momentan keine Freundschaftsanfragen!");
                        return;
                    } else if (requests.size() == 1) {
                        result.append("§7Du hast eine Freundschaftsanfrage von:\n");
                    } else {
                        result.append("§7Du hast ").append(requests.size()).append(" Freundschaftsanfragen von:\n");
                    }

                    appendListToStringBuilder(result, requests.values());

                    FriendSystem.getMessenger().send(bp, result.toString());
                    return;
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("req") || args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("requests")) {
                if (args[1].equalsIgnoreCase("toggle")) {
                    PlayerSettings settings = p.getSettings();

                    if (settings.isEnableFriendRequests()) {
                        settings.setEnableFriendRequests(false);
                        FriendSystem.getMessenger().send(bp, "§2Du hast Freundschaftsanfragen §aausgeschaltet!");
                    } else {
                        settings.setEnableFriendRequests(true);
                        FriendSystem.getMessenger().send(bp, "§2Du hast Freundschaftsanfragen §aeingeschaltet!");
                    }

                    p.updateSettings(settings);
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

                                            FriendSystem.getMessenger().send(bp, "§2Du bist nun mit §f" + target + " §2befreundet!");

                                            ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                            if (f != null) {
                                                FriendSystem.getMessenger().send(f, "§f" + p.getName() + "§2 hat deine Freundschaftanfrage angenommen!");
                                            }
                                        } else if (!t.getFriendData().getRequests().containsKey(p.getUuid())) {
                                            if (t.getSettings().isEnableFriendRequests()) {
                                                BungeeCoreSystem.getInstance().getFriendSystem().addRequest(BungeeCoreSystem.getInstance().getPlayerUtils().fetchUuid(target), p.getUuid(), p.getName());
                                                FriendSystem.getMessenger().send(bp, "§2Du hast §f" + target + "§2 eine Freundschaftsanfrage geschickt!");

                                                if (t.getFriendData().getFriends().size() > 44) {
                                                    FriendSystem.getMessenger().send(bp, "§c" + target + "§4 hat die maximale Anzahl an Freunden erreicht und kann gerade keine weiteren Freundschaftsanfragen annehmen! Die Anfrage wurde trotzdem versendet.");
                                                }

                                                ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                                if (f != null) {
                                                    f.sendMessage(
                                                            new ComponentBuilder("")
                                                                    .appendLegacy(Transl.get("system.prefix.friend", bp))
                                                                    .appendLegacy("§f" + p.getName() + "§7 hat dir eine Freundschaftanfrage geschickt!\n")
                                                                    .appendLegacy(Transl.get("system.prefix.friend", bp))
                                                                    .appendLegacy("§a[ANNEHMEN]")
                                                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§o/friend add " + p.getName()).create()))
                                                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend add " + p.getName()))
                                                                    .append(" ")
                                                                    .appendLegacy("§c[ABLEHNEN]")
                                                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§o/friend decline " + p.getName()).create()))
                                                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + p.getName()))
                                                                    .create()
                                                    );
                                                }
                                            } else {
                                                FriendSystem.getMessenger().send(bp, "§c" + target + "§4 hat Freundschaftsanfragen deaktiviert!");
                                            }
                                        } else {
                                            FriendSystem.getMessenger().send(bp, "§4Du hast §c" + target + "§4 bereits eine Freundschaftsanfrage geschickt!");
                                        }
                                    } else {
                                        FriendSystem.getMessenger().send(bp, "§c" + target + "§4 hat dich blockiert!");
                                    }
                                } else {
                                    FriendSystem.getMessenger().send(bp, "§4Du kannst nicht mehr als 44 Freunde gleichzeitig haben!");
                                }
                            } else {
                                FriendSystem.getMessenger().send(bp, "§4Du bist bereits mit §c" + target + "§4 befreundet!");
                            }
                            return;
                        }
                        case "delete":
                        case "remove": {
                            if (p.getFriendData().getFriends().containsKey(t.getUuid())) {
                                BungeeCoreSystem.getInstance().getFriendSystem().removeFriend(p.getUuid(), t.getUuid());
                                BungeeCoreSystem.getInstance().getFriendSystem().removeFriend(t.getUuid(), p.getUuid());
                                FriendSystem.getMessenger().send(bp, "§f" + target + "§2 wurde aus deiner §2Freundesliste entfernt!");

                                ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                if (f != null) {
                                    FriendSystem.getMessenger().send(f, "§c" + p.getName() + "§4 hat dich aus seiner Freundesliste entfernt!");
                                }
                            } else {
                                FriendSystem.getMessenger().send(bp, "§c" + target + "§4 befindet sich nicht in deiner Freundeliste!");
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

                                    FriendSystem.getMessenger().send(bp, "§2Du hast die Freundschaftsanfrage von §f" + target + " §2erfolgreich angenommen!");

                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                    if (f != null) {
                                        FriendSystem.getMessenger().send(f, "§f" + p.getName() + "§2 hat deine Freundschaftanfrage angenommen!");
                                    }
                                } else {
                                    FriendSystem.getMessenger().send(bp, "§c" + target + " §4hat dir keine Freundschaftsanfrage geschickt!");
                                }
                            } else {
                                FriendSystem.getMessenger().send(bp, "§4Du kannst nicht mehr als 44 Freunde gleichzeitig haben! Entferne einen Freund und versuche es erneut.");
                            }
                            return;
                        }
                        case "deny":
                        case "decline": {
                            Map<UUID, String> reqests = p.getFriendData().getRequests();

                            if (reqests.containsKey(t.getUuid())) {
                                BungeeCoreSystem.getInstance().getFriendSystem().removeRequest(p.getUuid(), t.getUuid());
                                FriendSystem.getMessenger().send(bp, "§2Du hast die Freundschaftsanfrage von §f" + target + " §2erfolgreich abgelehnt!");

                                ProxiedPlayer f = ProxyServer.getInstance().getPlayer(t.getUuid());
                                if (f != null) {
                                    FriendSystem.getMessenger().send(f, "§c" + p.getName() + "§4 hat deine Freundschaftanfrage abgelehnt!");
                                }
                            } else {
                                FriendSystem.getMessenger().send(bp, "§c" + target + " §4hat dir keine Freundschaftsanfrage geschickt!");
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
                                        FriendSystem.getMessenger().send(f, "§c" + p.getName() + "§4 hat dich aus seiner Freundesliste entfernt!");
                                    }
                                    FriendSystem.getMessenger().send(bp, "§2Du hast §f" + target + "§2 erfolgreich blockiert!");
                                } else {
                                    FriendSystem.getMessenger().send(bp, "§4Du hast §c" + target + "§4 bereits blockiert!");
                                }
                            } else {
                                FriendSystem.getMessenger().send(bp, "§4Du darft kein §cTeam-Mitglied §4blockieren!");
                            }
                            return;
                        }
                        case "unblock": {
                            if (p.getFriendData().getBlocks().contains(t.getUuid())) {
                                BungeeCoreSystem.getInstance().getFriendSystem().removeBlock(p.getUuid(), t.getUuid());
                                FriendSystem.getMessenger().send(bp, "§2Du hast §f" + target + "§2 erfolgreich aus deinen blockierten Spielern gelöscht!");
                            } else {
                                FriendSystem.getMessenger().send(bp, "§4Du hast §c" + target + "§4 nicht blockiert!");
                            }
                            return;
                        }
                    }
                } catch (CoreException e) {
                    Msg.send(bp, "§4Der Spielername §c" + args[0] + "§4 existiert nicht!");
                    return;
                }
            } else {
                FriendSystem.getMessenger().send(bp, "§4Bitte wähle einen anderen Spieler als dich selbst!");
                return;
            }
        }

        FriendSystem.getMessenger().send(bp, "§4Bitte benutze: §c/friend <list | requests | accept | deny | add | remove | block | unblock> §c[<name>] §4oder §c/friend request toggle");
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 1) {
                String search = args[0];
                Set<String> matches = new HashSet<>();

                for (String arg : new String[]{"list", "requests", "accept", "deny", "add", "remove", "block", "unblock"}) {
                    if (arg.startsWith(search)) {
                        matches.add(arg);
                    }
                }

                return matches;
            } else if (args.length == 2) {
                for (String arg : new String[]{"accept", "deny", "add", "remove", "block", "unblock"}) {
                    if (args[0].equalsIgnoreCase(arg)) {
                        String search = args[1];
                        Set<String> matches = new HashSet<>();
                        ProxiedPlayer p = (ProxiedPlayer) sender;
                        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

                        if (arg.equalsIgnoreCase("remove")) {
                            Set<UUID> friends = cp.getFriendData().getFriends().keySet();

                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                if (player != p && friends.contains(player.getUniqueId()) && player.getName().startsWith(search)) {
                                    matches.add(player.getName());
                                }
                            }
                        } else if (arg.equalsIgnoreCase("unblock")) {
                            List<UUID> friends = cp.getFriendData().getBlocks();

                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                if (player != p && friends.contains(player.getUniqueId()) && player.getName().startsWith(search)) {
                                    matches.add(player.getName());
                                }
                            }
                        } else if (arg.equalsIgnoreCase("deny")) {
                            Set<UUID> friends = cp.getFriendData().getRequests().keySet();

                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                if (player != p && friends.contains(player.getUniqueId()) && player.getName().startsWith(search)) {
                                    matches.add(player.getName());
                                }
                            }
                        } else {
                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                if (player != p && player.getName().startsWith(search)) {
                                    matches.add(player.getName());
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
