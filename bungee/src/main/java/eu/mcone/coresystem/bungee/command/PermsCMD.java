/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.lib.player.Group;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.lib.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PermsCMD extends Command implements TabExecutor {

    public PermsCMD(){
        super("perms", "system.bungee.perms", "permissions");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 5) {
            if (args[0].equals("user")) {
                final UUID target = UUIDFetcher.getUuidFromDatabase(CoreSystem.mysql1, args[1]);

                if (target != null) {
                    if (args[2].equals("group") && args[3].equals("set")) {
                        final Group group = Group.getGroupbyName(args[4]);

                        if (group != null) {
                            CoreSystem.mysql1.selectAsync("SELECT `uuid` FROM `userinfo` WHERE `uuid`='" + target + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        if (CoreSystem.getInstance().getPermissionManager().getGroups().contains(group)) {
                                            CoreSystem.mysql1.update("UPDATE userinfo SET gruppe='" + group.getName() + "' WHERE uuid='" + target + "'");
                                            Messager.send(sender, "§2Die Gruppe von " + args[1] + " wurde erfolgreich auf §f" + group.getLabel() + "§2 geändert!");

                                            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, CoreSystem.getCorePlayer(target), group));
                                            Messager.console(CoreSystem.MainPrefix + "§f" + sender.getName() + "§7 hat die Gruppe von §2" + args[1] + "§7 auf §f" + group.getLabel() + "§7 geändert!");
                                        } else {
                                            Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                                        }
                                    } else {
                                        Messager.send(sender, "§4Der Spieler §c" + args[1] + "§4 ist nicht in der Datenbank vorhanden!");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                        }
                    } else if (args[2].equals("add")) {
                        final String permission = args[3];
                        final String server = args[4];

                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + target.toString() + "', 'player-permission', '" + permission + "', '" + server + "')");
                        Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, CoreSystem.getCorePlayer(target)));
                        Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7 hat dem User §2"+args[1]+"§7 die Permission §f"+permission+"§7 auf dem Server §7§o"+server+"§7 hinzugefügt!");
                    } else if (args[2].equals("remove")) {
                        final String permission = args[3];
                        final String server = args[4];

                        CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + target.toString() + "' AND `key`='player-permission' AND `value`='" + permission + "' AND `server`='" + server + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + target.toString() + "' AND `key`='player-permission' AND `value`='" + permission + "' AND `server`='" + server + "'");
                                } else {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + target.toString() + "', 'player-permission', '-" + permission + "', '" + server + "')");
                                }
                                Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 entzogen!");

                                ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, CoreSystem.getCorePlayer(target)));
                                Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7 hat dem User §2"+args[1]+"§7 die Permission §f"+permission+"§7 auf dem Server §7§o"+server+"§7 entzogen!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    Messager.send(sender, "§c"+args[1]+"§4 war noch nie auf MC ONE!");
                }
                return;
            } else if (args[0].equals("group")) {
                final Group group = Group.getGroupbyName(args[1]);
                final String permission = args[3];
                final String server = args[4];

                if (group != null) {
                    switch (args[2]) {
                        case "add":
                        case "addperm": {
                            CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + group.getName() + "', 'permission', '" + permission + "', '" + server + "')");
                            Messager.send(sender, "§2Der Gruppe " + group.getLabel() + "§2 wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");

                            if (sender instanceof ProxiedPlayer) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, CoreSystem.getCorePlayer(((ProxiedPlayer) sender).getUniqueId()), group));
                            Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7 hat der Gruppe §f"+group.getLabel()+"§7 wurde die Permission §2"+permission+"§7 auf dem Server §7§o"+server+"§7 hinzugefügt");
                            break;
                        }
                        case "remove":
                        case "removeperm": {
                            CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + group.getName() + "' AND `key`='permission' AND `value`='" + permission + "' AND `server`='" + server + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + group.getName() + "' AND `key`='permission' AND `value`='" + permission + "' AND `server`='" + server + "'");
                                    } else {
                                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + group.getName() + "', 'player-permission', '-" + permission + "', '" + server + "')");
                                    }
                                    Messager.send(sender, "§2Der Gruppe " + group.getLabel() + "§2 wurde die Permission §f" + permission + " auf dem Server §f" + server + "§2 entzogen!");

                                    if (sender instanceof ProxiedPlayer) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, CoreSystem.getCorePlayer(((ProxiedPlayer) sender).getUniqueId()), group));
                                    Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7hat der Gruppe §f"+group.getLabel()+"§7 wurde die Permission §2"+permission+"§7 auf dem Server §7§o"+server+"§7 entzogen");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        }
                    }
                } else {
                    Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                }
                return;
            }
        } else if (args.length == 4) {
            if (args[0].equals("user")) {
                final UUID target = UUIDFetcher.getUuidFromDatabase(CoreSystem.mysql1, args[1]);
                final String permission = args[3];

                if (target != null) {
                    switch (args[2]) {
                        case "add":
                        case "addperm": {
                            CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + target.toString() + "', 'player-permission', '" + permission + "')");
                            Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 hinzugefügt!");

                            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, CoreSystem.getCorePlayer(target)));
                            Messager.console("§f"+sender.getName()+"§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                            break;
                        }
                        case "remove":
                        case "removeperm": {
                            CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + target.toString() + "' AND `key`='player-permission' AND `value`='" + permission + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + target.toString() + "' AND `key`='player-permission' AND `value`='" + permission + "'");
                                    } else {
                                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + target.toString() + "', 'player-permission', '-" + permission + "')");
                                    }
                                    Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 entzogen!");

                                    ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, CoreSystem.getCorePlayer(target)));
                                    Messager.console("§f"+sender.getName()+"§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 entfernt!");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        }
                        case "check": {
                            CoreSystem.mysql1.select("SELECT `value`, `server` FROM `bungeesystem_permissions` WHERE `name`='" + target.toString() + "' AND `key`='player-permission' AND `value`='" + permission + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        Messager.send(sender, "§2Der Spieler "+args[1]+" hat die Permission §f"+rs.getString("value")+"§2 auf dem Server §7"+rs.getString("server"));
                                    } else {
                                        Messager.send(sender, "§4Der Spieler "+args[1]+" hat die Permission §c"+permission+"§4 nicht!");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        }
                    }
                } else {
                    Messager.send(sender, "§c"+args[1]+"§4 war noch nie auf MC ONE!");
                }
                return;
            } else if (args[0].equals("group")) {
                final Group group = Group.getGroupbyName(args[1]);
                final String permission = args[3];

                if (group != null) {
                    switch (args[2]) {
                        case "add":
                        case "addperm": {
                            CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + group.getName() + "', 'permission', '" + permission + "')");
                            Messager.send(sender, "§2Der Gruppe " + group.getLabel() + "§2 wurde die Permission §f" + permission + "§2 hinzugefügt!");

                            if (sender instanceof ProxiedPlayer) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, CoreSystem.getCorePlayer(((ProxiedPlayer) sender).getUniqueId()), group));
                            Messager.console("§f"+sender.getName()+"§7 hat der Gruppe §f" + group.getLabel() + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                            break;
                        }
                        case "remove":
                        case "removeperm": {
                            CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + group.getName() + "' AND `key`='permission' AND `value`='" + permission + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + group.getName() + "' AND `key`='permission' AND `value`='" + permission + "'");
                                    } else {
                                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + group.getName() + "', 'player-permission', '-" + permission + "')");
                                    }
                                    Messager.send(sender, "§2Der Gruppe " + group.getLabel() + "§2 wurde die Permission §f" + permission + "§2 entzogen!");

                                    if (sender instanceof ProxiedPlayer) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, CoreSystem.getCorePlayer(((ProxiedPlayer) sender).getUniqueId()), group));
                                    Messager.console("§f"+sender.getName()+"§7 hat der Gruppe §f" + group.getLabel() + "§7 wurde die Permission §2" + permission + "§7 entfernt!");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        }
                        case "check": {
                            CoreSystem.mysql1.select("SELECT `value`, `server` FROM `bungeesystem_permissions` WHERE `name`='" + group.getName() + "' AND `key`='permission' AND `value`='" + permission + "'", rs -> {
                                try {
                                    if (rs.next()) {
                                        Messager.send(sender, "§2Die Gruppe " + group.getLabel() + "§2 hat die Permission §f" + rs.getString("value") + "§2 auf dem Server §7" + rs.getString("server"));
                                    } else {
                                        Messager.send(sender, "§4Die Gruppe " + group.getLabel() + "§4 hat die Permission §c" + permission + "§4 nicht!");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        }
                    }
                } else {
                    Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                }
                return;
            }
        } else if (args.length == 3) {
            if (args[2].equalsIgnoreCase("list")) {
                if (args[0].equalsIgnoreCase("user")) {
                    final String user = args[1];
                    final UUID uuid = UUIDFetcher.getUuidFromDatabase(CoreSystem.mysql1, user);

                    if (uuid != null) {
                        Messager.send(sender, "§7Der Spieler §f" + user + "§7 hat folgende Permissions:");

                        List<String> permissions = CoreSystem.getOfflinePlayer(uuid).getPermissions();
                        for (String permission : permissions) {
                            Messager.send(sender, "§f§o" + permission);
                        }
                    } else {
                        Messager.send(sender, "§4Dieser Spieler war noch nie auf MC ONE!");
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("group")) {
                    final Group group = Group.getGroupbyName(args[1]);

                    if (group != null) {
                        Messager.send(sender, "§7Die Gruppe §f" + group.getLabel() + "§7 hat folgende Permissions:");

                        List<String> permissions = CoreSystem.getInstance().getPermissionManager().getGroupPermissions(group);
                        for (String permission : permissions) {
                            Messager.send(sender, "§f§o" + permission);
                        }
                    } else {
                        Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                    }
                    return;
                }
            }
        }

        Messager.send(sender, "§4Bitte benutze: §c/perms user <user> group set <group> §4oder §c/perms <group | user> <group | user> <add | remove | check | list> [<permission>] [<server>]");
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("user", "group"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("user")) {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    result.add(p.getName());
                }
            } else if (args[0].equalsIgnoreCase("group")) {
                for (Group g : CoreSystem.getInstance().getPermissionManager().getGroups()) result.add(g.getName());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("user")) {
                result.add("group");
            }
            result.addAll(Arrays.asList("add", "remove", "check", "list"));
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("group")) {
                result.add("set");
            }
        }

        return result;
    }

}
