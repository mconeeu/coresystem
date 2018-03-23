/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.ban.BanManager;
import eu.mcone.coresystem.bungee.player.OfflinePlayer;
import eu.mcone.coresystem.lib.player.Group;
import eu.mcone.coresystem.bungee.utils.Messager;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PremiumCMD extends Command implements TabExecutor {
    public PremiumCMD() {
        super("premium", null);
    }

    public void execute(final CommandSender sender, final String[] args) {
        long unixtime = System.currentTimeMillis() / 1000;

        if (args.length == 0) {
            String[] parts = CoreSystem.sqlconfig.getConfigValue("CMD-Premium").split("%button%");

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
                    OfflinePlayer t = CoreSystem.getOfflinePlayer(target).loadPermissions();

                    if (t != null) {
                        if (args[0].equalsIgnoreCase("check")) {
                            CoreSystem.mysql1.select("SELECT * FROM `bungeesystem_premium` WHERE `uuid` = '" + t.getUuid().toString() + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        String rang = rs.getString("group");
                                        long timestamp = Long.parseLong(rs.getString("timestamp"));

                                        Messager.sendSimple(p, "");
                                        Messager.send(p, "§7Der Spieler §f" + target);
                                        Messager.send(p, "§7hat den Rang §6" + rang);
                                        Messager.send(p, "§7noch " + BanManager.getEndeString(timestamp));
                                    } else {
                                        Messager.send(p, "§7Der Spieler §f" + target + " §7hat keinen auslaufenden Rang.");
                                    }
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            });
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (t.hasPermission("mcone.premium")) {
                                CoreSystem.mysql1.select("SELECT * FROM bungeesystem_premium WHERE uuid='" + target + "'", rs -> {
                                    try {
                                        if (rs.next()) {
                                            String uuid = rs.getString("uuid");
                                            String group = rs.getString("group");
                                            String old_group = rs.getString("old_group");

                                            CoreSystem.mysql1.update("UPDATE userinfo SET gruppe='" + old_group + "' WHERE uuid='" + uuid + "'");

                                            CoreSystem.mysql1.select("SELECT gruppe FROM `userinfo` WHERE uuid='" + uuid + "'", rs_info -> {
                                                try {
                                                    if (rs_info.next()) {
                                                        String value = rs_info.getString("gruppe");

                                                        if (value.equalsIgnoreCase("Spieler")) {
                                                            CoreSystem.mysql1.update("DELETE FROM bungeesystem_premium WHERE uuid = '" + uuid + "';");
                                                            Messager.send(sender, "§2Dem Spieler " + target + " wurde der Rang §f" + group + "§2 erfolgreich entzogen!");
                                                        } else {
                                                            Messager.send(sender, "§7[§cMySQL ERROR§7] §4DER SPIELER KONNTE NICHT GELÖSCHT WERDEN OBWOHL SEIN PREMIUM RANG ABGLAUFEN IST!");
                                                        }
                                                    }
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                        } else {
                                            Messager.send(sender, "§4Dieser Spieler hat keinen auslaufenden Rang!");
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                Messager.send(sender, "§4Dieser Spieler hat keinen Premium Rang!");
                            }
                        }
                    } else {
                        Messager.send(sender, "§c" + target + "§4 war noch nie auf MC ONE!");
                    }
                    return;
                } else if (args.length == 3) {
                    String target = args[1];
                    Group group = Group.getGroupbyName(args[2]);
                    OfflinePlayer t = CoreSystem.getOfflinePlayer(target).loadPermissions();

                    if (group != null) {
                        if (t != null) {
                            if (args[0].equalsIgnoreCase("add")) {
                                if (!t.hasPermission("mcone.premium")) {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_premium` (`uuid`, `group`, `old_group`, `kosten`, `gekauft`, `timestamp`) VALUES ('" + t.getUuid().toString() + "', '" + group.getName() + "', '" + Group.getJson(t.getGroups()) + "', 'free', '" + unixtime + "', " + ((60 * 60 * 24 * 30) + unixtime) + ")");
                                    CoreSystem.mysql1.update("UPDAtE userinfo SET gruppe='" + group.getName() + "' WHERE uuid='" + target + "'");
                                    Messager.send(sender, "§2Dem Spieler " + target + " wurde der Rang §f" + group.getName() + " §2für 1 Monat zugeschrieben!");
                                } else {
                                    Messager.send(sender, "§4Dieser Spieler hat bereits einen Premium Rang");
                                }
                            }
                        } else {
                            Messager.send(sender, "§c" + target + "§4 war noch nie auf MC ONE!");
                        }
                    } else {
                        Messager.send(sender, "§4Dieser Rang existiert nicht!");
                    }
                    return;
                } else if (args.length == 4) {
                    String target = args[1];
                    Group group = Group.getGroupbyName(args[2]);
                    int months = Integer.valueOf(args[3]);
                    OfflinePlayer t = CoreSystem.getOfflinePlayer(target).loadPermissions();

                    if (group != null) {
                        if (t != null) {
                            if (args[0].equalsIgnoreCase("add")) {
                                if (!t.hasPermission("mcone.premium")) {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_premium` (`uuid`, `group`, `old_group`, `kosten`, `gekauft`, `timestamp`) VALUES ('" + t.getUuid().toString() + "', '" + group.getName() + "', '" + Group.getJson(t.getGroups()) + "', 'free', '" + unixtime + "', " + ((60 * 60 * 24 * 30 * months) + unixtime) + ")");
                                    CoreSystem.mysql1.update("UPDAtE userinfo SET gruppe='" + group.getName() + "' WHERE uuid='" + target + "'");
                                    Messager.send(sender, "§2Dem Spieler " + target + " wurde der Rang §f" + group.getName() + " §2für " + months + " Monat(e) zugeschrieben!");
                                } else {
                                    Messager.send(sender, "§4Dieser Spieler hat bereits einen Premium Rang");
                                }
                            }
                        } else {
                            Messager.send(sender, "§c" + target + "§4 war noch nie auf MC ONE!");
                        }
                    } else {
                        Messager.send(sender, "§4Dieser Rang existiert nicht!");
                    }
                    return;
                }

                Messager.send(p, "§4Bitte benutze: §c/premium add <player> <group> [<Anzahl der Monate>] §4oder §c/premium <check | remove> <player>");
            } else {
                Messager.send(p, CoreSystem.sqlconfig.getConfigValue("System-NoPerm"));
            }
        } else {
            Messager.send(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
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
                for (Group g : CoreSystem.getInstance().getPermissionManager().getGroups()) result.add(g.getName());
            }
        }

        return result;
    }
}
