/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.NpcManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class NpcCMD extends CorePlayerCommand {

    private NpcManager api;

    public NpcCMD(NpcManager api) {
        super("npc", "system.bukkit.world.npc");
        this.api = api;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            if (CoreSystem.getInstance().getNpcManager().getNPCs().size() > 0) {
                CoreSystem.getInstance().getMessager().send(p, "§7Folgende NPCs existieren auf diesem Server: ");

                for (CoreWorld w : BukkitCoreSystem.getInstance().getWorldManager().getWorlds()) {
                    if (w.getNPCs().size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder("\n§f[" + w.getName() + "]\n");

                        for (eu.mcone.coresystem.api.bukkit.npc.NPC npc : w.getNPCs()) {
                            componentBuilder
                                    .append(npc.getData().getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(npc.getData().getLocation().toString() + "\n§7Displayname: §3" + npc.getData().getDisplayname() + "\n§7Lokal: " + npc.isLocal() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tp " + w.getName() + " " + npc.getData().getName()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    }
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§7Auf dem Server existieren keine NPCs!");
            }

            return true;
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                StringBuilder line = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    line.append(args[i]);
                    if (i < args.length - 1) line.append(" ");
                }

                if (args[2].contains("Player~")) {
                    api.addNPC(args[1], line.toString().replaceAll("&", "§"), args[2].split("Player~")[1], NpcData.SkinKind.PLAYER, p.getLocation());
                } else {
                    api.addNPC(args[1], line.toString().replaceAll("&", "§"), args[2], NpcData.SkinKind.DATABASE, p.getLocation());
                }

                BukkitCoreSystem.getInstance().getMessager().send(p, "§2NPC §f" + args[1] + "§2 erfolgreich hinzugefügt!");
                return true;
            } else if (args[0].equalsIgnoreCase("update")) {
                StringBuilder line = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    line.append(args[i]);
                    if (i < args.length - 1) line.append(" ");
                }

                CoreWorld oldWord = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);
                if (oldWord != null) {
                    if (args[2].contains("Player~")) {
                        api.updateNPC(oldWord, args[2], p.getLocation(), args[3].split("Player~")[1], NpcData.SkinKind.PLAYER, line.toString().replaceAll("&", "§"));
                    } else {
                        api.updateNPC(oldWord, args[2], p.getLocation(), args[3], NpcData.SkinKind.DATABASE, line.toString().replaceAll("&", "§"));
                    }

                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2NPC §f" + args[2] + "§2 erfolgreich upgedated!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }
                return true;
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("remove")) {
                    CoreWorld oldWord = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                    if (oldWord != null) {
                        api.removeNPC(cp.getWorld(), args[2]);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2NPC §a" + args[2] + "§2 wurde erfolgreich aus der Welt " + oldWord.getName() + " gelöscht!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                    }

                    return true;
                } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                    CoreWorld w = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                    if (w != null) {
                        NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(w, args[2]);

                        if (npc != null) {
                            p.teleport(npc.getLocation());
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest zum NPC §a" + npc.getData().getName() + "§2 teleportiert!");
                        } else {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene NPC existiert nicht in der Welt §c" + w.getName() + "§4!");
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
                    List<eu.mcone.coresystem.api.bukkit.npc.NPC> npcs = w.getNPCs();

                    if (npcs.size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder(BukkitCoreSystem.getInstance().getTranslationManager().get("system.prefix.server"))
                                .append("Folgende NPCs existierten auf der Welt ").color(ChatColor.GRAY)
                                .append(args[1]).color(ChatColor.WHITE)
                                .append(": \n").color(ChatColor.GRAY);

                        for (eu.mcone.coresystem.api.bukkit.npc.NPC npc : npcs) {
                            componentBuilder
                                    .append(npc.getData().getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(npc.getData().getLocation().toString() + "\n§7Displayname: §3" + npc.getData().getDisplayname() + "\n§7Lokal: " + npc.isLocal() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tp " + npc.getData().getName()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§7Die Welt hat keine NPCs!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), args[1]);

                if (npc != null) {
                    p.teleport(npc.getLocation());
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wurdest zum NPC §a" + npc.getData().getName() + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Die angegebene NPC existiert nicht!");
                }
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            api.reload();
            BukkitCoreSystem.getInstance().getMessager().send(p, "§2NPCs erfolgreich neu geladen!");
            return true;
        }

        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: " +
                "\n§c/npc add <name> <texture-name> <display-name> §4oder " +
                "\n§c/npc remove <world-name> <name> §4oder " +
                "\n§c/npc update <world-name> <name> <texture-name> <display-name> §4oder " +
                "\n§c/npc list [world-name] §4oder " +
                "\n§c/npc tp [world-name] <name> §4oder " +
                "\n§c/npc reload §4oder "
        );

        return true;
    }

}
