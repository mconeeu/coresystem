/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.lib.util.UUIDFetcher;
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
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            final CorePlayer cp = CoreSystem.getCorePlayer(p);

            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (args.length == 1) {
                switch (args[0]) {
                    case "list": {
                        Map<UUID, String> friends = cp.getFriends();
                        StringBuilder result = new StringBuilder();

                        if (friends.size() == 0) {
                            Messager.sendFriend(p, "§7Du hast momentan keine Freunde!");
                            return;
                        } else if (friends.size() == 1) {
                            result.append("§7Du hast einen Freund:\n");
                        } else {
                            result.append("§7Du hast ").append(friends.size()).append(" Freunde:\n");
                        }

                        int i = 0;
                        for (String friend : friends.values()) {
                            i++;

                            if (i == friends.size()) {
                                result.append("§f§o").append(friend);
                                continue;
                            }
                            result.append("§f§o").append(friend).append("§7, ");
                        }

                        Messager.sendFriend(p, result.toString());
                        return;
                    }
                    case "req":
                    case "request":
                    case "requests": {
                        Map<UUID, String> requests = cp.getRequests();
                        StringBuilder result = new StringBuilder();

                        if (cp.hasRequestsToggled()) Messager.sendFriend(p, "§7§oDu hast Freundschaftsanfragen deaktiviert! Aktiviere sie mit §f/friend requests toggle");

                        if (requests.size() == 0) {
                            Messager.sendFriend(p, "§7Du hast momentan keine Freundschaftsanfragen!");
                            return;
                        } else if (requests.size() == 1) {
                            result.append("§7Du hast eine Freundschaftsanfrage von:\n");
                        } else {
                            result.append("§7Du hast ").append(requests.size()).append(" Freundschaftsanfragen von:\n");
                        }

                        int i = 0;
                        for (String request : requests.values()) {
                            i++;

                            if (i == requests.size()) {
                                result.append("§f§o").append(request);
                                continue;
                            }
                            result.append("§f§o").append(request).append("§7, ");
                        }

                        Messager.sendFriend(p, result.toString());
                        return;
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("req") ||args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("requests")){
                    if (args[1].equalsIgnoreCase("toggle")) {
                        System.out.println("unique id: "+p.getUniqueId().toString());
                        if (cp.hasRequestsToggled()) {
                            CoreSystem.getInstance().getFriendSystem().removeToggled(p.getUniqueId());
                            Messager.sendFriend(p, "§2Du hast Freundschaftsanfragen §feingeschaltet!");
                        } else {
                            CoreSystem.getInstance().getFriendSystem().addToggled(p.getUniqueId());
                            Messager.sendFriend(p, "§2Du hast Freundschaftsanfragen §fausgeschaltet!");
                        }
                    }
                    return;
                } else if (!p.getName().equalsIgnoreCase(args[1])) {
                    String target = args[1];
                    UUID targetUUID = UUIDFetcher.getUuidFromDatabase(CoreSystem.mysql1, target);

                    if (targetUUID != null) {
                        switch (args[0]) {
                            case "add": {
                                if (!cp.getFriends().containsKey(targetUUID)) {
                                    if (cp.getFriends().size() <= 44) {
                                        if (!CoreSystem.getOfflinePlayer(targetUUID).getBlocks().contains(p.getUniqueId())) {
                                            if (cp.getRequests().containsKey(targetUUID)) {
                                                CoreSystem.getInstance().getFriendSystem().addFriend(p.getUniqueId(), targetUUID, target);
                                                CoreSystem.getInstance().getFriendSystem().addFriend(targetUUID, p.getUniqueId(), p.getName());
                                                CoreSystem.getInstance().getFriendSystem().removeRequest(p.getUniqueId(), targetUUID);

                                                Messager.sendFriend(p, "§2Du bist nun mit §f" + target + " §2befreundet!");

                                                ProxiedPlayer f = ProxyServer.getInstance().getPlayer(targetUUID);
                                                if (f != null) {
                                                    Messager.sendFriend(f, "§f" + p.getName() + "§2 hat deine Freundschaftanfrage angenommen!");
                                                }
                                            } else if (!CoreSystem.getOfflinePlayer(targetUUID).getRequests().containsKey(p.getUniqueId())) {
                                                if (!CoreSystem.getOfflinePlayer(targetUUID).hasRequestsToggled()) {
                                                    CoreSystem.getInstance().getFriendSystem().addRequest(UUIDFetcher.getUuid(target), p.getUniqueId(), p.getName());
                                                    Messager.sendFriend(p, "§2Du hast §f" + target + "§2 eine Freundschaftsanfrage geschickt!");

                                                    if (CoreSystem.getOfflinePlayer(targetUUID).getFriends().size() > 44) {
                                                        Messager.sendFriend(p, "§c" + target + "§4 hat die maximale Anzahl an Freunden erreicht und kann keine weiteren Freundschaftsanfragen mehr annehmen!");
                                                    }

                                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(targetUUID);
                                                    if (f != null) {
                                                        f.sendMessage(
                                                                new ComponentBuilder("")
                                                                        .append(TextComponent.fromLegacyText(CoreSystem.sqlconfig.getConfigValue("Friend-Prefix")))
                                                                        .append(TextComponent.fromLegacyText("§f" + p.getName() + "§7 hat dir eine Freundschaftanfrage geschickt!\n"))
                                                                        .append(TextComponent.fromLegacyText(CoreSystem.sqlconfig.getConfigValue("Friend-Prefix")))
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
                                                    Messager.sendFriend(p, "§c" + target + "§4 hat Freundschaftsanfragen deaktiviert!");
                                                }
                                            } else {
                                                Messager.sendFriend(p, "§4Du hast §c" + target + "§4 bereits eine Freundschaftsanfrage geschickt!");
                                            }
                                        } else {
                                            Messager.sendFriend(p, "§c" + target + "§4 hat dich blockiert!");
                                        }
                                    } else {
                                        Messager.sendFriend(p, "§4Du kannst nicht mehr als 44 Freunde gleichzeitig haben!");
                                    }
                                } else {
                                    Messager.sendFriend(p, "§4Du bist bereits mit §c" + target + "§4 befreundet!");
                                }
                                return;
                            }
                            case "delete":
                            case "remove": {
                                if (cp.getFriends().containsKey(targetUUID)) {
                                    CoreSystem.getInstance().getFriendSystem().removeFriend(p.getUniqueId(), targetUUID);
                                    CoreSystem.getInstance().getFriendSystem().removeFriend(targetUUID, p.getUniqueId());
                                    Messager.sendFriend(p, "§f" + target + "§2 wurde aus deiner §2Freundesliste entfernt!");

                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(targetUUID);
                                    if (f != null) {
                                        Messager.sendFriend(f, "§c" + p.getName() + "§4 hat dich aus seiner Freundesliste entfernt!");
                                    }
                                } else {
                                    Messager.sendFriend(p, "§c" + target + "§4 befindet sich nicht in deiner Freundeliste!");
                                }
                                return;
                            }
                            case "accept": {
                                if (cp.getFriends().size() <= 44) {
                                    Map<UUID, String> reqests = cp.getRequests();

                                    if (reqests.containsKey(targetUUID)) {
                                        CoreSystem.getInstance().getFriendSystem().addFriend(p.getUniqueId(), targetUUID, target);
                                        CoreSystem.getInstance().getFriendSystem().addFriend(targetUUID, p.getUniqueId(), p.getName());
                                        CoreSystem.getInstance().getFriendSystem().removeRequest(p.getUniqueId(), targetUUID);

                                        Messager.sendFriend(p, "§2Du hast die Freundschaftsanfrage von §f" + target + " §2erfolgreich angenommen!");

                                        ProxiedPlayer f = ProxyServer.getInstance().getPlayer(targetUUID);
                                        if (f != null) {
                                            Messager.sendFriend(f, "§f" + p.getName() + "§2 hat deine Freundschaftanfrage angenommen!");
                                        }
                                    } else {
                                        Messager.sendFriend(p, "§c" + target + " §4hat dir keine Freundschaftsanfrage geschickt!");
                                    }
                                } else {
                                    Messager.sendFriend(p, "§4Du kannst nicht mehr als 44 Freunde gleichzeitig haben!");
                                }
                                return;
                            }
                            case "deny":
                            case "decline": {
                                Map<UUID, String> reqests = cp.getRequests();

                                if (reqests.containsKey(targetUUID)) {
                                    CoreSystem.getInstance().getFriendSystem().removeRequest(p.getUniqueId(), targetUUID);
                                    Messager.sendFriend(p, "§2Du hast die Freundschaftsanfrage von §f" + target + " §2erfolgreich abgelehnt!");

                                    ProxiedPlayer f = ProxyServer.getInstance().getPlayer(targetUUID);
                                    if (f != null) {
                                        Messager.sendFriend(f, "§c" + p.getName() + "§4 hat deine Freundschaftanfrage abgelehnt!");
                                    }
                                } else {
                                    Messager.sendFriend(p, "§c" + target + " §4hat dir keine Freundschaftsanfrage geschickt!");
                                }
                                return;
                            }
                            case "block": {
                                if (!CoreSystem.getOfflinePlayer(targetUUID).hasPermission("group.team")) {
                                    if (!cp.getBlocks().contains(targetUUID)) {
                                        CoreSystem.getInstance().getFriendSystem().addBlock(p.getUniqueId(), targetUUID);
                                        CoreSystem.getInstance().getFriendSystem().removeFriend(p.getUniqueId(), targetUUID);
                                        CoreSystem.getInstance().getFriendSystem().removeFriend(targetUUID, p.getUniqueId());

                                        ProxiedPlayer f = ProxyServer.getInstance().getPlayer(targetUUID);
                                        if (f != null) {
                                            Messager.sendFriend(f, "§c" + p.getName() + "§4 hat dich aus seiner Freundesliste entfernt!");
                                        }
                                        Messager.sendFriend(p, "§2Du hast §f" + target + "§2 erfolgreich blockiert!");
                                    } else {
                                        Messager.sendFriend(p, "§4Du hast §c" + target + "§4 bereits blockiert!");
                                    }
                                } else {
                                    Messager.sendFriend(p, "§4Du darft kein §cTeam-Mitglied §4blockieren!");
                                }
                                return;
                            }
                            case "unblock": {
                                if (cp.getBlocks().contains(targetUUID)) {
                                    CoreSystem.getInstance().getFriendSystem().removeBlock(p.getUniqueId(), targetUUID);
                                    Messager.sendFriend(p, "§2Du hast §f" + target + "§2 erfolgreich aus deinen blockierten Spielern gelöscht!");
                                } else {
                                    Messager.sendFriend(p, "§4Du hast §c" + target + "§4 nicht blockiert!");
                                }
                                return;
                            }
                        }
                    } else {
                        Messager.sendFriend(p, "§4Dieser Spieler war noch nie auf MC ONE!");
                        return;
                    }
                } else {
                    Messager.sendFriend(p, "§4Bitte wähle einen anderen Spieler als dich selbst!");
                    return;
                }
            }

            Messager.sendFriend(p, "§4Bitte benutze: §c/friend <list | requests | accept | deny | add | remove | block | unblock> §c[<name>] §4oder §c/friend request toggle");
        } else {
            Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("list", "requests", "accept", "deny", "add", "remove", "block", "unblock"));
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("list") || !args[0].equalsIgnoreCase("requests") || !args[0].equalsIgnoreCase("req") || !args[0].equalsIgnoreCase("request")) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    result.add(p.getName());
                }
            }
        }

        return result;
    }

}
