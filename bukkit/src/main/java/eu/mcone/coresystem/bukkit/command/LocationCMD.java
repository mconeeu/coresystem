/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.Map;

public class LocationCMD extends CorePlayerCommand {

    public LocationCMD() {
        super("location", "system.bukkit.world.location");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            ComponentBuilder componentBuilder =
                    new ComponentBuilder(BukkitCoreSystem.getInstance().getTranslationManager().get("system.prefix.server")).append("Folgende Locations existieren auf diesem Server: ").color(ChatColor.GRAY);

            for (CoreWorld w : BukkitCoreSystem.getInstance().getWorldManager().getWorlds()) {
                if (w.getLocations().size() > 0) {
                    componentBuilder.append("\n§f[" + w.getName() + "]\n");

                    for (Map.Entry<String, CoreLocation> loc : w.getLocations().entrySet()) {
                        componentBuilder
                                .append(loc.getKey())
                                .color(ChatColor.DARK_AQUA)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loc.getValue().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/location tp " + w.getName() + " " + loc.getKey()))
                                .append(", ")
                                .color(ChatColor.GRAY);
                    }
                }
            }

            p.spigot().sendMessage(componentBuilder.create());
            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld().setLocation(args[1], p.getLocation()).save();
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Location wurde erfolgreich abgespeichert");

                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    if (w.getLocations().size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder(BukkitCoreSystem.getInstance().getTranslationManager().get("system.prefix.server"))
                                .append("Folgende Locations existierten auf der Welt ").color(ChatColor.GRAY)
                                .append(args[1]).color(ChatColor.WHITE)
                                .append(":\n").color(ChatColor.GRAY);

                        for (Map.Entry<String, CoreLocation> loc : w.getLocations().entrySet()) {
                            componentBuilder
                                    .append(loc.getKey())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f" + loc.getValue().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/location tp " + loc.getKey()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§7Die Welt hat keine abgespeicherten Locations!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht. Bitte benutze §c/world");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w.getLocations().containsKey(args[1])) {
                    w.removeLocation(args[1]).save();
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Location wurde erfolgreich gelöscht");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Location existiert nicht in deiner aktuellen Welt! Benutze §c/location remove <world-name> <location-name>§4 zum Löschen einer Location von einer anderen Welt!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                CoreLocation loc = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld().getLocation(args[1]);

                if (loc != null) {
                    p.teleport(loc.bukkit());
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der Location §a" + args[1] + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Location §c" + args[1] + "§4 existiert nicht in dieser Welt! Benutze §c/location tp <world-name> <location-name>§4 zum teleportieren zu einer Location von einer anderen Welt!");
                }

                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    if (w.getLocations().containsKey(args[2])) {
                        w.removeLocation(args[2]).save();
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Location wurde erfolgreich gelöscht");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Location existiert nicht!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    CoreLocation loc = w.getLocation(args[2]);

                    if (loc != null) {
                        p.teleport(loc.bukkit());
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der Location §a" + args[1] + "§2 teleportiert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Location §c" + args[2] + "§4 existiert nicht in der Welt " + args[1] + "!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            }
        }

        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/location <set | remove | list | tp> [<world-name>] [<location-name>]");

        return true;
    }

}
