/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.world.CoreBlockLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.listener.BlockLocationListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class LocationCMD extends CorePlayerCommand {

    private final BlockLocationListener listener;

    public LocationCMD(CorePlugin plugin) {
        super("location", "system.bukkit.world.location", "loc");
        plugin.registerEvents(this.listener = new BlockLocationListener());
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            CoreSystem.getInstance().getMessager().send(p, "§7Folgende Locations existieren auf diesem Server: ");

            for (CoreWorld w : BukkitCoreSystem.getInstance().getWorldManager().getWorlds()) {
                if (w.getLocations().size() > 0 || w.getBlockLocations().size() > 0) {
                    ComponentBuilder componentBuilder = new ComponentBuilder("\n§f[" + w.getName() + "]\n");

                    for (Map.Entry<String, CoreLocation> loc : w.getLocations().entrySet()) {
                        componentBuilder
                                .append(loc.getKey())
                                .color(ChatColor.DARK_AQUA)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loc.getValue().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/location tp " + w.getName() + " " + loc.getKey()))
                                .append(", ")
                                .color(ChatColor.GRAY);
                    }
                    if (w.getBlockLocations().size() > 0) {
                        componentBuilder.append("\n");
                    }
                    for (Map.Entry<String, CoreBlockLocation> loc : w.getBlockLocations().entrySet()) {
                        componentBuilder
                                .append(loc.getKey())
                                .color(ChatColor.DARK_GREEN)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(loc.getValue().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/location tpblock " + w.getName() + " " + loc.getKey()))
                                .append(", ")
                                .color(ChatColor.GRAY);
                    }

                    p.spigot().sendMessage(componentBuilder.create());
                }
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld().setLocation(args[1], p.getLocation()).save();
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die Location wurde erfolgreich abgespeichert");

                return true;
            } else if (args[0].equalsIgnoreCase("setblock")) {
                listener.addBlockLocationEntry(p.getUniqueId(), new BlockLocationListener.BlockChangeEntry(
                        BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld(),
                        args[1]
                ));
                BukkitCoreSystem.getInstance().getMessager().send(p, "§7Bitte klicke mit der Hand auf einen Block um dessen Location unter diesem Namen einzuspeichern!");

                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    if (w.getLocations().size() > 0 || w.getBlockLocations().size() > 0) {
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
                        if (w.getBlockLocations().size() > 0) {
                            componentBuilder.append("\n");
                        }
                        for (Map.Entry<String, CoreBlockLocation> loc : w.getBlockLocations().entrySet()) {
                            componentBuilder
                                    .append(loc.getKey())
                                    .color(ChatColor.DARK_GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f" + loc.getValue().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/location tpblock " + loc.getKey()))
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
            } else if (args[0].equalsIgnoreCase("removeblock")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w.getBlockLocations().containsKey(args[1])) {
                    w.removeBlockLocation(args[1]).save();
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die BlockLocation wurde erfolgreich gelöscht");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene BlockLocation existiert nicht in deiner aktuellen Welt! Benutze §c/location removeblock <world-name> <location-name>§4 zum Löschen einer BlockLocation von einer anderen Welt!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();
                Location loc = w.getLocation(args[1]);

                if (loc != null) {
                    p.teleport(loc);
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der Location §a" + args[1] + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Location §c" + args[1] + "§4 existiert nicht in dieser Welt! Benutze §c/location tp <world-name> <location-name>§4 zum teleportieren zu einer Location von einer anderen Welt!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tpblock") || args[0].equalsIgnoreCase("teleportblock")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getCorePlayer(p).getWorld();
                Location loc = w.getBlockLocation(args[1]);

                if (loc != null) {
                    p.teleport(loc);
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der BlockLocation §a" + args[1] + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die BlockLocation §c" + args[1] + "§4 existiert nicht in dieser Welt! Benutze §c/location tpblock <world-name> <location-name>§4 zum teleportieren zu einer BlockLocation von einer anderen Welt!");
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
            } else if (args[0].equalsIgnoreCase("removeblock")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    if (w.getBlockLocations().containsKey(args[2])) {
                        w.removeBlockLocation(args[2]).save();
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Die BlockLocation wurde erfolgreich gelöscht");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene BlockLocation existiert nicht!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    Location loc = w.getLocation(args[2]);

                    if (loc != null) {
                        p.teleport(loc);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der Location §a" + args[1] + "§2 teleportiert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die Location §c" + args[2] + "§4 existiert nicht in der Welt " + args[1] + "!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tpblock") || args[0].equalsIgnoreCase("teleportblock")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    Location loc = w.getBlockLocation(args[2]);

                    if (loc != null) {
                        p.teleport(loc);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest erfolgreich zu der BlockLocation §a" + args[1] + "§2 teleportiert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die BlockLocation §c" + args[2] + "§4 existiert nicht in der Welt " + args[1] + "!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            }
        }

        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/location <set[block] | remove[block] | list[block] | tp[block]> [<world-name>] [<location-name>]");

        return true;
    }

}
