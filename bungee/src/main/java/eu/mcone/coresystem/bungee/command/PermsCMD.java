/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.player.BungeeOfflineCorePlayer;
import eu.mcone.coresystem.core.mysql.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.SQLException;
import java.util.*;

public class PermsCMD extends Command implements TabExecutor {

    public PermsCMD() {
        super("perms", "system.bungee.perms", "permissions");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("user")) {
            try {
                OfflineCorePlayer p = new BungeeOfflineCorePlayer(BungeeCoreSystem.getSystem(), args[1]).loadPermissions();

                if (args.length == 5 && args[2].equalsIgnoreCase("group")) {
                    Group g = Group.getGroupbyName(args[4]);

                    if (g != null) {
                        if (args[3].equalsIgnoreCase("set")) {
                            BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET groups='[" + g.getId() + "]' WHERE uuid='" + p.getUuid() + "'");
                            BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Die Gruppe von " + args[1] + " wurde erfolgreich auf §f" + g.getLabel() + "§2 geändert!");

                            CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                            if (cp != null)
                                ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, cp, new HashSet<>(Collections.singletonList(g))));
                            BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat die Gruppe von §2" + args[1] + "§7 auf §f" + g.getLabel() + "§7 geändert!");
                            return;
                        } else if (args[3].equalsIgnoreCase("add")) {
                            Set<Group> groups = p.getGroups();

                            if (!groups.contains(g)) {
                                groups.add(g);

                                StringBuilder sb = new StringBuilder();
                                groups.forEach(group -> sb.append(group.getLabel()).append(" "));

                                BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET groups='" + BungeeCoreSystem.getInstance().getPermissionManager().getJson(groups) + "' WHERE uuid='" + p.getUuid() + "'");
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der User " + args[1] + " besitzt nun die Gruppen: " + sb.toString() + "§7(Gruppe " + g.getName() + " hinzugefügt)");

                                CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                                if (cp != null)
                                    ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, cp, groups));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat die Gruppen von §2" + args[1] + "§7 geändert: " + sb.toString() + "§7(Gruppe " + g.getName() + " hinzugefügt)");
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler " + p.getName() + " hat die Gruppe " + g.getLabel() + "§4 bereits!");
                            }
                            return;
                        } else if (args[3].equalsIgnoreCase("remove")) {
                            Set<Group> groups = p.getGroups();

                            if (groups.contains(g)) {
                                groups.remove(g);

                                StringBuilder sb = new StringBuilder();
                                groups.forEach(group -> sb.append(group.getLabel()).append(" "));

                                BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET groups='" + BungeeCoreSystem.getInstance().getPermissionManager().getJson(groups) + "' WHERE uuid='" + p.getUuid() + "'");
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der User " + args[1] + " besitzt nun die Gruppen: " + sb.toString() + "§7(Gruppe " + g.getName() + " gelöscht)");

                                CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                                if (cp != null)
                                    ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, cp, groups));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat die Gruppen von §2" + args[1] + "§7 geändert: " + sb.toString() + "§7(Gruppe " + g.getName() + " gelöscht)");
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler " + p.getName() + " hat die Gruppe " + g.getLabel() + "§4 nicht!");
                            }
                            return;
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Diese Gruppe existiert nicht!");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("addperm") || args[2].equalsIgnoreCase("add")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`) VALUES ('" + p.getUuid() + "', 'eu.mcone.coresystem.api.core.player-permission', '" + permission + "')");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                        if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`, `server`) VALUES ('" + p.getUuid() + "', 'eu.mcone.coresystem.api.core.player-permission', '" + permission + "', '" + server + "')");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                        if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat dem User §2"+args[1]+"§7 die Permission §f"+permission+"§7 auf dem Server §7§o"+server+"§7 hinzugefügt!");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("removeperm") || args[2].equalsIgnoreCase("remove")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `id` FROM `permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='eu.mcone.coresystem.api.core.player-permission' AND `value`='" + permission + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM `permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='eu.mcone.coresystem.api.core.player-permission' AND `value`='" + permission + "'");
                                } else {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`) VALUES ('" + p.getUuid() + "', 'eu.mcone.coresystem.api.core.player-permission', '-" + permission + "')");
                                }
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 entzogen!");
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                                if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 entfernt!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `id` FROM `permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='eu.mcone.coresystem.api.core.player-permission' AND `value`='" + permission + "' AND `server`='" + server + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM `permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='eu.mcone.coresystem.api.core.player-permission' AND `value`='" + permission + "' AND `server`='" + server + "'");
                                } else {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`, `server`) VALUES ('" + p.getUuid() + "', 'eu.mcone.coresystem.api.core.player-permission', '-" + permission + "', '" + server + "')");
                                }
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 entzogen!");
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUuid());
                                if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat dem User §2"+args[1]+"§7 die Permission §f"+permission+"§7 auf dem Server §7§o"+server+"§7 entzogen!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }
                } else if (args.length == 4 && args[2].equalsIgnoreCase("check")) {
                    String permission = args[3];

                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `value`, `server` FROM `permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='eu.mcone.coresystem.api.core.player-permission' AND `value`='" + permission + "'", rs -> {
                        try {
                            if (rs.next()) {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Spieler "+args[1]+" hat die Permission §f"+rs.getString("value")+"§2 auf dem Server §7"+rs.getString("server"));
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler "+args[1]+" hat die Permission §c"+permission+"§4 nicht!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                }
            } catch (CoreException e) {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler "+args[1]+" war noch nie auf MC ONE!");
                return;
            }
        } else if (args.length >=3 && args[0].equalsIgnoreCase("group")) {
            Group g = Group.getGroupbyName(args[1]);

            if (g != null) {
                if (args[2].equalsIgnoreCase("addperm") || args[2].equalsIgnoreCase("add")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`) VALUES ('" + g.getId() + "', 'permission', '" + permission + "')");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat der Gruppe §f" + g.getLabel() + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`, `server`) VALUES ('" + g.getId() + "', 'permission', '" + permission + "', '" + server + "')");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat der Gruppe §f"+g.getLabel()+"§7 wurde die Permission §2"+permission+"§7 auf dem Server §7§o"+server+"§7 hinzugefügt");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("removeperm") || args[2].equalsIgnoreCase("remove")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `id` FROM `permissions` WHERE `name`='" + g.getId() + "' AND `key`='permission' AND `value`='" + permission + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM `permissions` WHERE `name`='" + g.getId() + "' AND `key`='permission' AND `value`='" + permission + "'");
                                } else {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`) VALUES ('" + g.getId() + "', 'permission', '-" + permission + "')");
                                }
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 entzogen!");
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7 hat der Gruppe §f" + g.getLabel() + "§7 wurde die Permission §2" + permission + "§7 entfernt!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `id` FROM `permissions` WHERE `name`='" + g.getId() + "' AND `key`='permission' AND `value`='" + permission + "' AND `server`='" + server + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM `permissions` WHERE `name`='" + g.getId() + "' AND `key`='permission' AND `value`='" + permission + "' AND `server`='" + server + "'");
                                } else {
                                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO `permissions` (`name`, `key`, `value`, `server`) VALUES ('" + g.getId() + "', 'permission', '-" + permission + "', '" + server + "')");
                                }
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + " auf dem Server §f" + server + "§2 entzogen!");
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f"+sender.getName()+"§7hat der Gruppe §f"+g.getLabel()+"§7 wurde die Permission §2"+permission+"§7 auf dem Server §7§o"+server+"§7 entzogen");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }
                } else if (args.length == 4 && args[2].equalsIgnoreCase("check")) {
                    final String permission = args[3];

                    BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `value`, `server` FROM `permissions` WHERE `name`='" + g.getId() + "' AND `key`='permission' AND `value`='" + permission + "'", rs -> {
                        try {
                            if (rs.next()) {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Die Gruppe " + g.getLabel() + "§2 hat die Permission §f" + rs.getString("value") + "§2 auf dem Server §7" + rs.getString("server"));
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Die Gruppe " + g.getLabel() + "§4 hat die Permission §c" + permission + "§4 nicht!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                }
            } else {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Diese Gruppe existiert nicht!");
                return;
            }
        }

        BungeeCoreSystem.getInstance().getMessager().send(sender,
                "§4Bitte benutze: " +
                        "\n§c/perms user <user> <group [set, add, remove] | addperm | removeperm | check> [<group | permission>] §4oder" +
                "\n§c/perms group <group> <addperm | removeperm | check> [<permission>]"
        );
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("user", "group"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("user")) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    result.add(p.getName());
                }
            } else if (args[0].equalsIgnoreCase("group")) {
                for (Group g : BungeeCoreSystem.getInstance().getPermissionManager().getGroups()) result.add(g.getName());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("user")) result.add("group");
            result.addAll(Arrays.asList("addperm", "removeperm", "check", "list"));
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("group")) {
                result.addAll(Arrays.asList("set", "add", "remove"));
            }
        } else if (args.length == 5) {
            if (args[3].equalsIgnoreCase("set")) {
                for (Group g : BungeeCoreSystem.getInstance().getPermissionManager().getGroups()) result.add(g.getName());
            }
        }

        return result;
    }

}
