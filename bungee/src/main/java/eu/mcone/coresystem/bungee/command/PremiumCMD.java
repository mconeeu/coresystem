/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PremiumCMD extends Command implements TabExecutor {

    public PremiumCMD() {
        super("premium", null);
    }

    public void execute(final CommandSender sender, final String[] args) {
        long unixtime = System.currentTimeMillis() / 1000;

        if (args.length == 0) {
            String[] parts = BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.command.premium").split("%button%");

            sender.sendMessage(
                    new ComponentBuilder("")
                            .append(TextComponent.fromLegacyText(parts[0]))
                            .append("§7» §3§l§nZum Shop")
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oShop im Webbrowser öffnen").create()))
                            .event(new ClickEvent(Action.OPEN_URL, "https://shop.mcone.eu"))
                            .append(TextComponent.fromLegacyText(parts[1]))
                            .create()
            );
        } else if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;

            if (p.hasPermission("system.bungee.premium")) {
                if (args.length == 2) {
                    String target = args[1];

                    try {
                        OfflineCorePlayer t = BungeeCoreSystem.getInstance().getOfflineCorePlayer(target);

                        if (args[0].equalsIgnoreCase("check")) {
                            Document entry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").find(eq("uuid", t.getUuid().toString())).first();

                            if (entry != null) {
                                String rang = entry.getString("group");
                                long timestamp = entry.getLong("timestamp");

                                BungeeCoreSystem.getInstance().getMessenger().sendSimple(p, "");
                                BungeeCoreSystem.getInstance().getMessenger().send(p, "§7Der Spieler §f" + target);
                                BungeeCoreSystem.getInstance().getMessenger().send(p, "§7hat den Rang §6" + rang);
                                BungeeCoreSystem.getInstance().getMessenger().send(p, "§7noch " + BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getEndeString(timestamp));
                            } else {
                                BungeeCoreSystem.getInstance().getMessenger().send(p, "§7Der Spieler §f" + target + " §7hat keinen auslaufenden Rang.");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            try {
                                if (t.hasPermission("mcone.premium")) {
                                    Document removeEntry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").find(eq("uuid", t.getUuid().toString())).first();

                                    if (removeEntry != null) {
                                        Group group = Group.getGroupById(removeEntry.getInteger("group"));
                                        t.removeGroup(group);

                                        if (!t.updateGroupsFromDatabase().contains(group)) {
                                            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").deleteOne(eq("uuid", t.getUuid().toString()));
                                            BungeeCoreSystem.getInstance().getMessenger().send(sender, "§2Dem Spieler " + target + " wurde der Rang §f" + group + "§2 erfolgreich entzogen!");
                                        } else {
                                            throw new CoreException("Premium Rank of player " + t.getName() + " could not be removed. Error in code!");
                                        }
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Dieser Spieler hat keinen auslaufenden Rang!");
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Dieser Spieler hat keinen Premium Rang!");
                                }
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }

                        return;
                    } catch (PlayerNotResolvedException e) {
                        BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Der Spielername §c" + args[0] + "§4 existiert nicht!");
                    }
                } else if (args.length == 3) {
                    String target = args[1];
                    Group group = Group.getGroupbyName(args[2]);

                    try {
                        OfflineCorePlayer t = BungeeCoreSystem.getInstance().getOfflineCorePlayer(target);

                        if (group != null) {
                            if (args[0].equalsIgnoreCase("add")) {
                                if (!t.hasPermission("mcone.premium")) {
                                    BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").insertOne(
                                            new Document("uuid", t.getUuid().toString())
                                                    .append("group", group.getId())
                                                    .append("buyed", unixtime)
                                                    .append("timestamp", ((60 * 60 * 24 * 30) + unixtime))
                                    );
                                    t.addGroup(group);

                                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§2Dem Spieler " + target + " wurde der Rang §f" + group.getName() + " §2für 1 Monat zugeschrieben!");
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Dieser Spieler hat bereits einen Premium Rang");
                                }
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Dieser Rang existiert nicht!");
                        }
                        return;
                    } catch (CoreException e) {
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Der Spielername §c" + target + "§4 existiert nicht!");
                    }
                } else if (args.length == 4) {
                    String target = args[1];
                    Group group = Group.getGroupbyName(args[2]);
                    int months = Integer.valueOf(args[3]);

                    try {
                        OfflineCorePlayer t = BungeeCoreSystem.getInstance().getOfflineCorePlayer(target);

                        if (group != null) {
                            if (args[0].equalsIgnoreCase("add")) {
                                if (!t.hasPermission("mcone.premium")) {
                                    BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").insertOne(
                                            new Document("uuid", t.getUuid().toString())
                                                    .append("group", group.getId())
                                                    .append("buyed", unixtime)
                                                    .append("timestamp", ((60 * 60 * 24 * 30 * months) + unixtime))
                                    );
                                    t.addGroup(group);

                                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§2Dem Spieler " + target + " wurde der Rang §f" + group.getName() + " §2für " + months + " Monat(e) zugeschrieben!");
                                } else {
                                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Dieser Spieler hat bereits einen Premium Rang");
                                }
                            }
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Dieser Rang existiert nicht!");
                        }
                        return;
                    } catch (CoreException e) {
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Der Spielername §c" + target + "§4 existiert nicht!");
                    }
                }

                BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/premium add <player> <group> [<Anzahl der Monate>] §4oder §c/premium <check | remove> <eu.mcone.coresystem.api.core.player>");
            } else {
                BungeeCoreSystem.getInstance().getMessenger().send(p, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.noperm"));
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("add", "ckeck"));
        } else if (args.length == 2) {
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                result.add(p.getName());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add")) {
                for (Group g : BungeeCoreSystem.getInstance().getPermissionManager().getGroups())
                    result.add(g.getName());
            }
        }

        return result;
    }
}
