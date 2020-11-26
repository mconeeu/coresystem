/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import com.google.gson.JsonSyntaxException;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Transl;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.CoreNpcManager;
import eu.mcone.coresystem.bukkit.npc.NpcType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NpcCMD extends CorePlayerCommand {

    private final CoreNpcManager api;

    public NpcCMD(CoreNpcManager api) {
        super("npc", "system.bukkit.world.npc");
        this.api = api;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            if (api.getNpcs().size() > 0) {
                CoreSystem.getInstance().getMessenger().send(p, "§7Folgende NPCs existieren auf diesem Server: ");

                for (CoreWorld w : BukkitCoreSystem.getInstance().getWorldManager().getWorlds()) {
                    if (w.getNPCs().size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder("\n§f[" + w.getName() + "]\n");

                        for (NPC npc : w.getNPCs()) {
                            componentBuilder
                                    .append(npc.getData().getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(npc.getData().getLocation().toString() + "\n§7Displayname: §3" + npc.getData().getDisplayname() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tp " + w.getName() + " " + npc.getData().getName()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    }
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§7Auf dem Server existieren keine NPCs!");
            }

            return true;
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("update")) {
            StringBuilder line = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                line.append(args[i]);
                if (i < args.length - 1) line.append(" ");
            }

            CoreWorld w = cp.getWorld();
            NPC npc = api.getNPC(w, args[1]);

            if (npc != null) {
                api.updateAndSave(npc, line.toString(), p.getLocation());
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§2NPC §f" + args[1] + "§2 erfolgreich upgedated!");
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein NPC mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
            }

            return true;
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("add")) {
            StringBuilder line = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                line.append(args[i]);
                if (i < args.length - 1) line.append(" ");
            }

            api.addNPCAndSave(EntityType.valueOf(args[1]), args[2], line.toString().replaceAll("&", "§"), p.getLocation());
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2NPC §f" + args[1] + "§2 erfolgreich hinzugefügt!");
            return true;
        } else if (args.length == 4) {

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("updateData")) {
                CoreWorld w = cp.getWorld();
                NPC npc = api.getNPC(w, args[1]);

                if (npc != null) {
                    try {
                        api.updateDataAndSave(npc, CoreSystem.getInstance().getJsonParser().parse(args[2]));
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Daten des NPCs §f" + args[1] + "§2 erfolgreich upgedated!");
                    } catch (JsonSyntaxException e) {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ungültiger JSON Syntax: " + e.getMessage());
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein NPC mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                CoreWorld w = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(w, args[2]);

                    if (npc != null) {
                        p.teleport(npc.getData().getLocation().bukkit());
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du wurdest zum NPC §a" + npc.getData().getName() + "§2 teleportiert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene NPC existiert nicht in der Welt §c" + w.getName() + "§4!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("animation")) {
                CoreWorld w = cp.getWorld();
                NPC npc = api.getNPC(cp.getWorld(), args[1]);

                if (npc != null) {
                    try {
                        npc.sendAnimation(NpcAnimation.valueOf(args[2]));
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Der NPC §a" + args[1] + "§2 in der Welt " + w.getName() + " hat erfolgreich die Animation §7" + args[2] + "§2 ausgeführt!");
                    } catch (IllegalArgumentException e) {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Eine Animation mit dem Namen §c" + args[2] + "§4 existiert nicht!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein NPC mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }

                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
                CoreWorld w = cp.getWorld();
                NPC npc = api.getNPC(cp.getWorld(), args[1]);

                if (npc != null) {
                    api.removeNPCAndSave(npc);
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2NPC §a" + args[1] + "§2 wurde erfolgreich aus der Welt " + w.getName() + " gelöscht!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Ein NPC mit dem Namen §c" + args[1] + "§4 existiert nicht in der Welt " + w.getName() + "!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                CoreWorld w = CoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    List<NPC> npcs = w.getNPCs();

                    if (npcs.size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder(Transl.get("system.prefix.server", p))
                                .append("Folgende NPCs existierten auf der Welt ").color(ChatColor.GRAY)
                                .append(args[1]).color(ChatColor.WHITE)
                                .append(": \n").color(ChatColor.GRAY);

                        for (NPC npc : npcs) {
                            componentBuilder
                                    .append(npc.getData().getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(npc.getData().getLocation().toString() + "\n§7Displayname: §3" + npc.getData().getDisplayname() + "\n§7§oLinksklick zum teleportieren").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tp " + npc.getData().getName()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    } else {
                        BukkitCoreSystem.getInstance().getMessenger().send(p, "§7Die Welt hat keine NPCs!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                NPC npc = CoreSystem.getInstance().getNpcManager().getNPC(cp.getWorld(), args[1]);

                if (npc != null) {
                    p.teleport(npc.getData().getLocation().bukkit());
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du wurdest zum NPC §a" + npc.getData().getName() + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Die angegebene NPC existiert nicht!");
                }

                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            api.reload();
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2NPCs erfolgreich neu geladen!");
            return true;
        }

        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: " +
                "\n§c/npc add <entity-type> <name> <display-name> §4oder " +
                "\n§c/npc update <name> <display-name> §4oder " +
                "\n§c/npc updateData <name> <{} JSON-Data> §4oder " +
                "\n§c/npc list [<world>] §4oder " +
                "\n§c/npc tp [<world>] <name> §4oder " +
                "\n§c/npc remove <name> §4oder " +
                "\n§c/npc animation <name> <animation> §4oder" +
                "\n§c/npc reload §4oder "
        );

        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"add", "update", "updateData", "list", "tp", "remove", "animation", "reload"}) {
                if (arg.startsWith(search)) {
                    matches.add(arg);
                }
            }

            return matches;
        } else if (args.length == 2) {
            String search = args[1];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"update", "updateData", "remove", "animation", "tp"}) {
                if (args[0].equalsIgnoreCase(arg)) {
                    for (NPC npc : api.getNpcs()) {
                        if (npc.getData().getName().startsWith(search)) {
                            matches.add(npc.getData().getName());
                        }
                    }

                    if (args[0].equalsIgnoreCase("tp")) {
                        for (CoreWorld world : CoreSystem.getInstance().getWorldManager().getWorlds()) {
                            if (world.getName().startsWith(search)) {
                                matches.add(world.getName());
                            }
                        }
                    }

                    return matches;
                }
            }

            if (args[0].equalsIgnoreCase("add")) {
                for (NpcType type : NpcType.values()) {
                    if (type.getType().name().startsWith(search)) {
                        matches.add(type.getType().name());
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                for (CoreWorld world : CoreSystem.getInstance().getWorldManager().getWorlds()) {
                    if (world.getName().startsWith(search)) {
                        matches.add(world.getName());
                    }
                }
            }

            return matches;
        } else if (args.length == 3 && args[2].equalsIgnoreCase("animation")) {
            String search = args[1];
            List<String> matches = new ArrayList<>();

            for (NpcAnimation animation : NpcAnimation.values()) {
                if (animation.name().startsWith(search)) {
                    matches.add(animation.name());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }

}
