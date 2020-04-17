/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoloCMD extends CorePlayerCommand {

    private CoreHologramManager api;

    public HoloCMD(CoreHologramManager api) {
        super("holo", "system.bukkit.world.holo", "hologram");
        this.api = api;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            if (api.getHologramSet().size() > 0) {
                CoreSystem.getInstance().getMessenger().send(p, "§7Folgende Hologramme existieren auf diesem Server: ");

                for (CoreWorld w : BukkitCoreSystem.getInstance().getWorldManager().getWorlds()) {
                    if (w.getHolograms().size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder("\n§f[" + w.getName() + "]\n");

                        for (eu.mcone.coresystem.api.bukkit.hologram.Hologram holo : w.getHolograms()) {
                            componentBuilder
                                    .append(holo.getData().getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(holo.getData().getLocation().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/holo tp " + w.getName() + " " + holo.getData().getName()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    }
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§7Auf dem Server existieren keine Hologramme!");
            }

            return true;
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                StringBuilder line = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    line.append(args[i]);
                    if (i < args.length - 1) line.append(" ");
                }

                api.addHologramAndSave(args[1], p.getLocation(), line.toString());
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Hologramm §f" + args[1] + "§2 erfolgreich hinzugefügt!");
                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                CoreWorld w = BukkitCoreSystem.getSystem().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    eu.mcone.coresystem.api.bukkit.hologram.Hologram holo = CoreSystem.getInstance().getHologramManager().getHologram(w, args[2]);

                    if (holo != null) {
                        p.teleport(holo.getData().getLocation().bukkit());
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du wurdest zum Hologramm §a" + holo.getData().getName() + "§2 teleportiert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Das angegebene Hologramm existiert nicht in der Welt §c" + w.getName() + "§4!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("addline") || args[0].equalsIgnoreCase("al")) {
                StringBuilder line = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    line.append(args[i]);
                    if (i < args.length - 1) line.append(" ");
                }

                CoreWorld w = cp.getWorld();
                Hologram holo = api.getHologram(w, args[1]);

                if (holo != null) {
                    List<String> text = new ArrayList<>(Arrays.asList(holo.getData().getText()));
                    text.add(line.toString());

                    api.updateAndSave(holo, text.toArray(new String[0]), holo.getData().getLocation().bukkit());
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Die Zeile wurde hinzugefügt!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein Hologram mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("setline") || args[0].equalsIgnoreCase("sl")) {
                StringBuilder line = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    line.append(args[i]);
                    if (i < args.length - 1) line.append(" ");
                }

                CoreWorld w = cp.getWorld();
                Hologram holo = api.getHologram(w, args[1]);

                if (holo != null) {
                    try {
                        int i = Integer.parseInt(args[2]);
                        List<String> text = new ArrayList<>(Arrays.asList(holo.getData().getText()));

                        if (i <= text.size()) {
                            text.set(i-1, line.toString());

                            api.updateAndSave(holo, text.toArray(new String[0]), holo.getData().getLocation().bukkit());
                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Die Zeile §a" + args[2] + "§2 wurde gesetzt!");
                        } else {
                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Das Hologram §o" + args[1] + "§4 hat nur §c" + text.size() + " Zeilen§4!");
                        }
                    } catch (NumberFormatException ignored) {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§c" + args[2] + "§4 ist keine Zahl!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein Hologram mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("removeline") || args[0].equalsIgnoreCase("deleteline") || args[0].equalsIgnoreCase("delline") || args[0].equalsIgnoreCase("dl") || args[0].equalsIgnoreCase("rl")) {
                CoreWorld w = cp.getWorld();
                Hologram holo = api.getHologram(w, args[1]);

                if (holo != null) {
                    try {
                        int i = Integer.parseInt(args[2]);
                        List<String> text = new ArrayList<>(Arrays.asList(holo.getData().getText()));

                        if (i <= text.size()) {
                            text.remove(i-1);

                            api.updateAndSave(holo, text.toArray(new String[0]), holo.getData().getLocation().bukkit());
                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Die Zeile §a" + args[2] + "§2 wurde gelöscht!");
                        } else {
                            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Das Hologram §o" + args[1] + "§4 hat nur §c" + text.size() + " Zeilen§4!");
                        }
                    } catch (NumberFormatException ignored) {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§c" + args[2] + "§4 ist keine Zahl!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein Hologram mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setlocation") || args[0].equalsIgnoreCase("sl") || args[0].equalsIgnoreCase("setposition") || args[0].equalsIgnoreCase("sp")) {
                CoreWorld w = cp.getWorld();
                Hologram holo = api.getHologram(w, args[1]);

                if (holo != null) {
                    api.updateAndSave(holo, holo.getData().getText(), p.getLocation());
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast das Hologram erfolgreich zu deiner Position verschoben!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein Hologram mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                CoreWorld w = cp.getWorld();
                Hologram holo = api.getHologram(w, args[1]);

                if (holo != null) {
                    api.removeHologramAndSave(holo);
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Hologramm §a" + args[1] + "§2 wurde erfolgreich aus der Welt " + w.getName() + " gelöscht!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein Hologram mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                CoreWorld w = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    List<eu.mcone.coresystem.api.bukkit.hologram.Hologram> holos = w.getHolograms();

                    if (holos.size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder(BukkitCoreSystem.getInstance().getTranslationManager().get("system.prefix.server"))
                                .append("Folgende Hologramme existierten auf der Welt ").color(ChatColor.GRAY)
                                .append(args[1]).color(ChatColor.WHITE)
                                .append(": \n").color(ChatColor.GRAY);

                        for (eu.mcone.coresystem.api.bukkit.hologram.Hologram holo : holos) {
                            componentBuilder
                                    .append(holo.getData().getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(holo.getData().getLocation().toString() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/holo tp " + holo.getData().getName()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    } else {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§7Die Welt hat keine Hologramme!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                Hologram holo = CoreSystem.getInstance().getHologramManager().getHologram(cp.getWorld(), args[1]);

                if (holo != null) {
                    p.teleport(holo.getData().getLocation().bukkit());
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du wurdest zum Hologramm §a" + holo.getData().getName() + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene Hologramm existiert nicht!");
                }
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            api.reload();
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Hologramme erfolgreich neu geladen!");
            return true;
        }

        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: " +
                "\n§c/holo add <name> <display-name> §4oder " +
                "\n§c/holo remove <name> §4oder " +
                "\n§c/holo setline <holo-name> <line> <text> §4oder " +
                "\n§c/holo addline <holo-name> <text> §4oder " +
                "\n§c/holo removeline <holo-name> <line> §4oder " +
                "\n§c/holo setlocation <holo-name>§4oder " +
                "\n§c/holo list [world-name] §4oder " +
                "\n§c/holo tp [world-name] <name> §4oder " +
                "\n§c/holo reload §4oder "
        );

        return true;
    }

}
