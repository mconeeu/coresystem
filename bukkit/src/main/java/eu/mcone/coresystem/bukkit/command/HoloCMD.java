/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.hologram.HologramManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class HoloCMD extends CorePlayerCommand {

    private HologramManager api;

    public HoloCMD(HologramManager api) {
        super("holo", "system.bukkit.world.holo");
        this.api = api;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            if (api.getHolograms().size() > 0) {
                CoreSystem.getInstance().getMessager().send(p, "§7Folgende Hologramme existieren auf diesem Server: ");

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
                BukkitCoreSystem.getInstance().getMessager().send(p, "§7Auf dem Server existieren keine Hologramme!");
            }

            return true;
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                StringBuilder line = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    line.append(args[i]);
                    if (i < args.length - 1) line.append(" ");
                }

                api.addHologram(args[1], p.getLocation(), line.toString());
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hologramm §f" + args[1] + "§2 erfolgreich hinzugefügt!");
                return true;
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("remove")) {
                    CoreWorld oldWord = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                    if (oldWord != null) {
                        api.removeHologram(cp.getWorld(), args[2]);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hologramm §a" + args[2] + "§2 wurde erfolgreich aus der Welt " + oldWord.getName() + " gelöscht!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                    }

                    return true;
                } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                    CoreWorld w = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                    if (w != null) {
                        eu.mcone.coresystem.api.bukkit.hologram.Hologram holo = CoreSystem.getInstance().getHologramManager().getHologram(w, args[2]);

                        if (holo != null) {
                            p.teleport(holo.getLocation());
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest zum Hologramm §a" + holo.getData().getName() + "§2 teleportiert!");
                        } else {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Das angegebene Hologramm existiert nicht in der Welt §c" + w.getName() + "§4!");
                        }
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                    }

                    return true;
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
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
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§7Die Welt hat keine Hologramme!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                Hologram holo = CoreSystem.getInstance().getHologramManager().getHologram(cp.getWorld(), args[1]);

                if (holo != null) {
                    p.teleport(holo.getLocation());
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest zum Hologramm §a" + holo.getData().getName() + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Hologramm existiert nicht!");
                }
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            api.reload();
            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hologramme erfolgreich neu geladen!");
            return true;
        }

        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: " +
                "\n§c/holo add <name> <display-name> §4oder " +
                "\n§c/holo remove <world-name> <name> §4oder " +
                "\n§c/holo list [world-name] §4oder " +
                "\n§c/holo tp [world-name] <name> §4oder " +
                "\n§c/holo reload §4oder "
        );

        return true;
    }

}
