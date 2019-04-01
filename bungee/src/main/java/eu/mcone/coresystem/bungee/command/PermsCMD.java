/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class PermsCMD extends Command implements TabExecutor {

    private final MongoCollection<Document> groupCollection;
    private final MongoCollection<Document> playerCollection;

    public PermsCMD(MongoDatabase database) {
        super("perms", "system.bungee.perms", "permissions");
        this.groupCollection = database.getCollection("permission_groups");
        this.playerCollection = database.getCollection("permission_players");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("user")) {
            try {
                OfflineCorePlayer p = CoreSystem.getInstance().getOfflineCorePlayer(args[1]);

                if (args.length == 5 && args[2].equalsIgnoreCase("group")) {
                    Group g = Group.getGroupbyName(args[4]);

                    if (g != null) {
                        if (args[3].equalsIgnoreCase("set")) {
                            HashSet<Group> groups = new HashSet<>(Collections.singletonList(g));

                            p.setGroups(groups);
                            BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Die Gruppe von " + args[1] + " wurde erfolgreich auf §f" + g.getLabel() + "§2 geändert!");

                            BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat die Gruppe von §2" + args[1] + "§7 auf §f" + g.getLabel() + "§7 geändert!");
                            return;
                        } else if (args[3].equalsIgnoreCase("add")) {
                            Set<Group> groups = new HashSet<>(p.getGroups());

                            if (!groups.contains(g)) {
                                groups.add(g);

                                StringBuilder sb = new StringBuilder();
                                groups.forEach(group -> sb.append(group.getLabel()).append(" "));

                                p.setGroups(groups);
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der User " + args[1] + " besitzt nun die Gruppen: " + sb.toString() + "§7(Gruppe " + g.getName() + " hinzugefügt)");

                                if (p instanceof BungeeCorePlayer)
                                    ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, (BungeeCorePlayer) p, groups));
                                BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat die Gruppen von §2" + args[1] + "§7 geändert: " + sb.toString() + "§7(Gruppe " + g.getName() + " hinzugefügt)");
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler " + p.getName() + " hat die Gruppe " + g.getLabel() + "§4 bereits!");
                            }
                            return;
                        } else if (args[3].equalsIgnoreCase("remove")) {
                            Set<Group> groups = new HashSet<>(p.getGroups());

                            if (groups.contains(g)) {
                                groups.remove(g);

                                StringBuilder sb = new StringBuilder();
                                groups.forEach(group -> sb.append(group.getLabel()).append(" "));

                                p.setGroups(groups);
                                BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der User " + args[1] + " besitzt nun die Gruppen: " + sb.toString() + "§7(Gruppe " + g.getName() + " gelöscht)");

                                if (p instanceof BungeeCorePlayer)
                                    ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_CHANGE, (BungeeCorePlayer) p, groups));
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
                    String permission = args[3].replace('.', '-');

                    if (args.length == 4) {
                        playerCollection.updateOne(
                                eq("uuid", p.getUuid().toString()),
                                set("permissions." + permission, null),
                                new UpdateOptions().upsert(true)
                        );
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        if (p instanceof BungeeCorePlayer)
                            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, (BungeeCorePlayer) p));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat dem Spieler §f" + args[1] + "§7 wurde die Permission §2" + permission + "§7 hinzugefügt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        playerCollection.updateOne(
                                eq("uuid", p.getUuid().toString()),
                                set("permissions." + permission, server),
                                new UpdateOptions().upsert(true)
                        );
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        if (p instanceof BungeeCorePlayer)
                            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, (BungeeCorePlayer) p));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat dem User §2" + args[1] + "§7 die Permission §f" + permission + "§7 auf dem Server §7§o" + server + "§7 hinzugefügt!");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("removeperm") || args[2].equalsIgnoreCase("remove")) {
                    String permission = args[3].replace('.', '-');

                    if (args.length == 4) {
                        Document permissionEntry = playerCollection.find(eq("uuid", p.getUuid().toString())).first();
                        if (permissionEntry != null && permissionEntry.get("permissions", new Document()).containsKey(permission)) {
                            playerCollection.updateOne(
                                    eq("uuid", p.getUuid().toString()),
                                    unset("permissions." + permission)
                            );
                        } else {
                            playerCollection.updateOne(
                                    eq("uuid", p.getUuid().toString()),
                                    set("permissions.-" + permission, null),
                                    new UpdateOptions().upsert(true)
                            );
                        }

                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 entzogen!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        if (p instanceof BungeeCorePlayer)
                            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, (BungeeCorePlayer) p));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat dem Spieler §f" + args[1] + "§7 die Permission §2" + permission + "§7 entfernt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        Document permissionEntry = playerCollection.find(eq("uuid", p.getUuid().toString())).first();
                        if (permissionEntry != null && permissionEntry.get("permissions", new Document()).containsKey(permission)) {
                            playerCollection.updateOne(
                                    eq("uuid", p.getUuid().toString()),
                                    unset("permissions." + permission)
                            );
                        } else {
                            playerCollection.updateOne(
                                    eq("uuid", p.getUuid().toString()),
                                    set("permissions.-" + permission, server),
                                    new UpdateOptions().upsert(true)
                            );
                        }

                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Dem Spieler " + args[1] + " wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 entzogen!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        if (p instanceof BungeeCorePlayer)
                            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.USER_PERMISSION, (BungeeCorePlayer) p));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat dem User §2" + args[1] + "§7 die Permission §f" + permission + "§7 auf dem Server §7§o" + server + "§7 entzogen!");
                        return;
                    }
                } else if (args.length == 4 && args[2].equalsIgnoreCase("check")) {
                    String permission = args[3].replace('.', '-');

                    Document permissionEntry = playerCollection.find(eq("uuid", p.getUuid().toString())).first();
                    Map<String, String> permissions;

                    if (permissionEntry != null && (permissions = permissionEntry.get("permissions", new HashMap<>())).containsKey(permission)) {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§7Der Spieler " + args[1] + " besitzt die eingetragene Spieler-Permission §f" + permission + "§7 auf dem Server §7§o" + permissions.get(permission));
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§7Der Spieler " + args[1] + " besitzt keine eingetragene Permission mit dem Namen §f" + permission + "§7!");
                    }
                    return;
                }
            } catch (CoreException e) {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spielername §c" + args[0] + "§4 existiert nicht!");
                return;
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("group")) {
            Group g = Group.getGroupbyName(args[1]);

            if (g != null) {
                if (args[2].equalsIgnoreCase("addperm") || args[2].equalsIgnoreCase("add")) {
                    String permission = args[3].replace('.', '-');

                    if (args.length == 4) {
                        groupCollection.updateOne(
                                eq("id", g.getId()),
                                set("permissions." + permission, null)
                        );
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat der Gruppe §f" + g.getLabel() + "§7 die Permission §2" + permission + "§7 hinzugefügt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        groupCollection.updateOne(
                                eq("id", g.getId()),
                                set("permissions." + permission, server)
                        );
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 auf dem Server §f" + server + "§2 hinzugefügt!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat der Gruppe §f" + g.getLabel() + "§7 die Permission §2" + permission + "§7 auf dem Server §7§o" + server + "§7 hinzugefügt");
                        return;
                    }
                } else if (args[2].equalsIgnoreCase("removeperm") || args[2].equalsIgnoreCase("remove")) {
                    String permission = args[3].replace('.', '-');

                    if (args.length == 4) {
                        Document permissionEntry = groupCollection.find(eq("id", g.getId())).first();
                        if (permissionEntry != null && permissionEntry.get("permissions", new Document()).containsKey(permission)) {
                            groupCollection.updateOne(
                                    eq("id", g.getId()),
                                    unset("permissions." + permission)
                            );
                        } else {
                            groupCollection.updateOne(
                                    eq("id", g.getId()),
                                    set("permissions.-" + permission, null)
                            );
                        }

                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + "§2 entzogen!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7 hat der Gruppe §f" + g.getLabel() + "§7 die Permission §2" + permission + "§7 entfernt!");
                        return;
                    } else if (args.length == 5) {
                        String server = args[4];

                        Document permissionEntry = groupCollection.find(eq("id", g.getId())).first();
                        if (permissionEntry != null && permissionEntry.get("permissions", new Document()).containsKey(permission)) {
                            groupCollection.updateOne(
                                    eq("id", g.getId()),
                                    unset("permissions." + permission)
                            );
                        } else {
                            groupCollection.updateOne(
                                    eq("id", g.getId()),
                                    set("permissions.-" + permission, server)
                            );
                        }

                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Der Gruppe " + g.getLabel() + "§2 wurde die Permission §f" + permission + " auf dem Server §f" + server + "§2 entzogen!");
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4§lBevor die Änderung übernommen ist müssen bei allen entsprechenden Servern die Permissions neu geladen werden!");

                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Kind.GROUP_PERMISSION, g));
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§f" + sender.getName() + "§7hat der Gruppe §f" + g.getLabel() + "§7 die Permission §2" + permission + "§7 auf dem Server §7§o" + server + "§7 entzogen");
                        return;
                    }
                } else if (args.length == 4 && args[2].equalsIgnoreCase("check")) {
                    final String permission = args[3].replace('.', '-');

                    Document permissionEntry = groupCollection.find(eq("id", g.getId())).first();
                    Map<String, String> permissions;

                    if (permissionEntry != null && (permissions = permissionEntry.get("permissions", new HashMap<>())).containsKey(permission)) {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§2Die Gruppe " + args[1] + " besitzt die Permission §a" + permission + "§2 auf dem Server §7§o" + permissions.get(permission));
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Die Gruppe " + args[1] + " besitzt die Permission §c" + permission + "§4 nicht!");
                    }
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
                for (Group g : BungeeCoreSystem.getInstance().getPermissionManager().getGroups())
                    result.add(g.getName());
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
                for (Group g : BungeeCoreSystem.getInstance().getPermissionManager().getGroups())
                    result.add(g.getName());
            }
        }

        return result;
    }

}
