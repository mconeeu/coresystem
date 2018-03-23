/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.player.OfflinePlayer;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.lib.player.Group;
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
            OfflinePlayer p = CoreSystem.getOfflinePlayer(args[1]).loadPermissions();

            if (p != null) {
                if (args.length == 5 && args[2].equalsIgnoreCase("group") && args[3].equalsIgnoreCase("set")) {
                    Group g = Group.getGroupbyName(args[4]);

                    if (g != null) {
                        CoreSystem.mysql1.update("UPDATE userinfo SET gruppe='" + g.getName() + "' WHERE uuid='"+p.getUuid()+"'");
                        Messager.send(sender, "§2Die Gruppe von " + args[1] + " wurde erfolgreich auf §f" + g.getLabel() + "§2 geändert!");

                        CorePlayer cp = CoreSystem.getCorePlayer(p.getUuid());
                        if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, cp, new HashSet<>(Collections.singletonList(g))));
                        Messager.console(CoreSystem.MainPrefix + "§f" + sender.getName() + "§7 hat die Gruppe von §2" + args[1] + "§7 auf §f" + g.getLabel() + "§7 geändert!");
                    } else {
                        Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                    }
                    return;
                } else if (args[2].equalsIgnoreCase("addperm") || args[2].equalsIgnoreCase("add")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + p.getUuid() + "', 'player-permission', '" + permission + "')");
                        Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 hinzugefügt!");
                        Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        CorePlayer cp = CoreSystem.getCorePlayer(p.getUuid());
                        if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                        Messager.console("§f"+sender.getName()+"§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + p.getUuid() + "', 'player-permission', '" + permission + "', '" + server + "')");
                        Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");
                        Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        CorePlayer cp = CoreSystem.getCorePlayer(p.getUuid());
                        if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                        Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7 hat dem User §2"+args[1]+"§7 die Permission §f"+permission+"§7 auf dem Server §7§o"+server+"§7 hinzugefügt!");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("removeperm") || args[2].equalsIgnoreCase("remove")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='player-permission' AND `value`='" + permission + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='player-permission' AND `value`='" + permission + "'");
                                } else {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + p.getUuid() + "', 'player-permission', '-" + permission + "')");
                                }
                                Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 entzogen!");
                                Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                CorePlayer cp = CoreSystem.getCorePlayer(p.getUuid());
                                if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                                Messager.console("§f"+sender.getName()+"§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 entfernt!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='player-permission' AND `value`='" + permission + "' AND `server`='" + server + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='player-permission' AND `value`='" + permission + "' AND `server`='" + server + "'");
                                } else {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + p.getUuid() + "', 'player-permission', '-" + permission + "', '" + server + "')");
                                }
                                Messager.send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 entzogen!");
                                Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                CorePlayer cp = CoreSystem.getCorePlayer(p.getUuid());
                                if (cp != null) ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, cp));
                                Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7 hat dem User §2"+args[1]+"§7 die Permission §f"+permission+"§7 auf dem Server §7§o"+server+"§7 entzogen!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }
                }/* else if (args.length <= 4 && args[2].equalsIgnoreCase("list")) {
                    List<String> permissions = p.getPermissions();
                    StringBuilder sb = new StringBuilder();
                    int index = 0;

                    sb.append("§7Der Spieler ").append(p.getName()).append("§7 hat folgende Permissions §8[§fSeite ");
                    if (args.length == 4) {
                        index = Integer.valueOf(args[3]);
                        sb.append(args[3]).append("§8]");

                        if (index == 1) {
                            index = 0;
                        } else {
                            index = (index-1)*20;
                        }
                    } else {
                        sb.append("1§8]");
                    }

                    while (index < 20 && index < permissions.size()) {
                        sb.append("\n§f§o").append(permissions.get(index));
                        index++;
                    }

                    Messager.send(sender, sb.toString());
                    return;
                }*/ else if (args.length == 4 && args[2].equalsIgnoreCase("check")) {
                    String permission = args[3];

                    CoreSystem.mysql1.select("SELECT `value`, `server` FROM `bungeesystem_permissions` WHERE `name`='" + p.getUuid() + "' AND `key`='player-permission' AND `value`='" + permission + "'", rs -> {
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
                    return;
                }
            } else {
                Messager.send(sender, "§4Dieser Spieler war noch nie auf MC ONE!");
                return;
            }
        } else if (args.length >=3 && args[0].equalsIgnoreCase("group")) {
            Group g = Group.getGroupbyName(args[1]);

            if (g != null) {
                if (args[2].equalsIgnoreCase("addperm") || args[2].equalsIgnoreCase("add")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + g.getName() + "', 'permission', '" + permission + "')");
                        Messager.send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 hinzugefügt!");
                        Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        Messager.console("§f"+sender.getName()+"§7 hat der Gruppe §f" + g.getLabel() + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + g.getName() + "', 'permission', '" + permission + "', '" + server + "')");
                        Messager.send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");
                        Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7 hat der Gruppe §f"+g.getLabel()+"§7 wurde die Permission §2"+permission+"§7 auf dem Server §7§o"+server+"§7 hinzugefügt");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("removeperm") || args[2].equalsIgnoreCase("remove")) {
                    String permission = args[3];

                    if (args.length == 4) {
                        CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + g.getName() + "' AND `key`='permission' AND `value`='" + permission + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + g.getName() + "' AND `key`='permission' AND `value`='" + permission + "'");
                                } else {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`) VALUES ('" + g.getName() + "', 'player-permission', '-" + permission + "')");
                                }
                                Messager.send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 entzogen!");
                                Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                                Messager.console("§f"+sender.getName()+"§7 hat der Gruppe §f" + g.getLabel() + "§7 wurde die Permission §2" + permission + "§7 entfernt!");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        CoreSystem.mysql1.select("SELECT `id` FROM `bungeesystem_permissions` WHERE `name`='" + g.getName() + "' AND `key`='permission' AND `value`='" + permission + "' AND `server`='" + server + "'", rs -> {
                            try {
                                if (rs.next()) {
                                    CoreSystem.mysql1.update("DELETE FROM `bungeesystem_permissions` WHERE `name`='" + g.getName() + "' AND `key`='permission' AND `value`='" + permission + "' AND `server`='" + server + "'");
                                } else {
                                    CoreSystem.mysql1.update("INSERT INTO `bungeesystem_permissions` (`name`, `key`, `value`, `server`) VALUES ('" + g.getName() + "', 'player-permission', '-" + permission + "', '" + server + "')");
                                }
                                Messager.send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + " auf dem Server §f" + server + "§2 entzogen!");
                                Messager.send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                                ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                                Messager.console(CoreSystem.MainPrefix + "§f"+sender.getName()+"§7hat der Gruppe §f"+g.getLabel()+"§7 wurde die Permission §2"+permission+"§7 auf dem Server §7§o"+server+"§7 entzogen");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        return;
                    }
                } /*else if (args.length <= 4 && args[2].equalsIgnoreCase("list")) {
                    List<String> permissions = new ArrayList<>(CoreSystem.getInstance().getPermissionManager().getGroupPermissions(g));
                    for (Group group : CoreSystem.getInstance().getPermissionManager().getParents(g)) {
                        permissions.addAll(CoreSystem.getInstance().getPermissionManager().getGroupPermissions(group));
                    }

                    StringBuilder sb = new StringBuilder();
                    int index = 0;

                    sb.append("§7Die Gruppe ").append(g.getLabel()).append("§7 hat folgende Permissions §8[§fSeite ");
                    if (args.length == 4) {
                        index = Integer.valueOf(args[3]);
                        sb.append(args[3]).append("§8]");

                        if (index == 1) {
                            index = 0;
                        } else {
                            index = (index-1)*20;
                        }
                    } else {
                        sb.append("1§8]");
                    }

                    while (index < 20 && index < permissions.size()) {
                        sb.append("\n§f§o").append(permissions.get(index));
                        index++;
                    }

                    Messager.send(sender, sb.toString());
                    return;
                }*/ else if (args.length == 4 && args[2].equalsIgnoreCase("check")) {
                    final String permission = args[3];

                    CoreSystem.mysql1.select("SELECT `value`, `server` FROM `bungeesystem_permissions` WHERE `name`='" + g.getName() + "' AND `key`='permission' AND `value`='" + permission + "'", rs -> {
                        try {
                            if (rs.next()) {
                                Messager.send(sender, "§2Die Gruppe " + g.getLabel() + "§2 hat die Permission §f" + rs.getString("value") + "§2 auf dem Server §7" + rs.getString("server"));
                            } else {
                                Messager.send(sender, "§4Die Gruppe " + g.getLabel() + "§4 hat die Permission §c" + permission + "§4 nicht!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                }
            } else {
                Messager.send(sender, "§4Diese Gruppe existiert nicht!");
                return;
            }
        }

        Messager.send(sender,
                "§4Bitte benutze: " +
                "\n§c/perms user <user> <group set | addperm | removeperm | check> [<group | permission>] §4oder" +
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
                for (Group g : CoreSystem.getInstance().getPermissionManager().getGroups()) result.add(g.getName());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("user")) result.add("group");
            result.addAll(Arrays.asList("addperm", "removeperm", "check", "list"));
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("group")) {
                result.add("set");
            }
        } else if (args.length == 5) {
            if (args[3].equalsIgnoreCase("set")) {
                for (Group g : CoreSystem.getInstance().getPermissionManager().getGroups()) result.add(g.getName());
            }
        }

        return result;
    }

}
