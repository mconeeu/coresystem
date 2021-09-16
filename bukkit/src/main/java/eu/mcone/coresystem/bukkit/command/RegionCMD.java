/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.world.CoreBlockLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.Region;
import eu.mcone.coresystem.api.bukkit.world.WorldManager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.entity.Player;

import java.util.*;

public class RegionCMD extends CorePlayerCommand {

    private static final Map<String, Region.Selection> SELECTION_RULES = new HashMap<String, Region.Selection>() {{
        put("set", Region.Selection.CUBIC);
        put("setCube", Region.Selection.CUBIC);
        put("setRect", Region.Selection.RECTANGULAR);
        put("setSphere", Region.Selection.SPHERICAL);
        put("setCircle", Region.Selection.CIRCULAR);
    }};

    private final WorldManager manager;

    public RegionCMD(WorldManager manager) {
        super("region", "system.bukkit.world.region", "regions", "rg");
        this.manager = manager;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            Msg.send(p, "§7Folgende Regionen existieren auf diesem Server: ");

            for (CoreWorld w : manager.getWorlds()) {
                if (w.getRegions().size() > 0) {
                    ComponentBuilder componentBuilder = new ComponentBuilder("\n§f[" + w.getName() + "]\n");

                    for (Region region : w.getRegions()) {
                        componentBuilder
                                .append(region.getName())
                                .color(ChatColor.DARK_AQUA)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(region.toString()).create()))
                                .append(", ")
                                .color(ChatColor.GRAY);
                    }

                    p.spigot().sendMessage(componentBuilder.create());
                }
            }

            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    if (w.getRegions().size() > 0) {
                        ComponentBuilder componentBuilder = new ComponentBuilder("\n§f[" + w.getName() + "]\n");

                        for (Region region : w.getRegions()) {
                            componentBuilder
                                    .append(region.getName())
                                    .color(ChatColor.DARK_AQUA)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(region.toString()).create()))
                                    .append(", ")
                                    .color(ChatColor.GRAY);
                        }

                        p.spigot().sendMessage(componentBuilder.create());
                    } else {
                        Msg.sendError(p, "Die Welt hat keine abgespeicherten Regionen!");
                    }
                } else {
                    Msg.send(p, "§4Die angegebene Welt existiert nicht. Bitte benutze §c/world");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                CoreWorld w = BukkitCoreSystem.getSystem().getCorePlayer(p).getWorld();
                Region region = w.getRegion(args[1]);

                if (region != null) {
                    w.removeRegion(args[1]).save();
                    Msg.send(p, "§2Die Region wurde erfolgreich gelöscht");
                } else {
                    Msg.send(p, "§4Die angegebene Region existiert nicht in deiner aktuellen Welt! Benutze §c/region remove <world-name> <region-name>§4 zum Löschen einer Region von einer anderen Welt!");
                }

                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                CoreWorld w = BukkitCoreSystem.getInstance().getWorldManager().getWorld(args[1]);

                if (w != null) {
                    Region region = w.getRegion(args[2]);

                    if (region != null) {
                        w.removeRegion(args[2]).save();
                        Msg.send(p, "§2Die Region wurde erfolgreich gelöscht");
                    } else {
                        Msg.send(p, "§4Die angegebene Region existiert nicht!");
                    }
                } else {
                    Msg.send(p, "§4Die angegebene Welt existiert nicht!");
                }

                return true;
            }
        } else if (args.length == 6 || args.length == 8) {
            for (Map.Entry<String, Region.Selection> rule : SELECTION_RULES.entrySet()) {
                if (rule.getKey().equalsIgnoreCase(args[0])) {
                    setRegion(BukkitCoreSystem.getSystem().getCorePlayer(p).getWorld(), rule.getValue(), args);
                    Msg.sendSuccess(p, "Region !["+args[1]+"] erfolgreich erstellt!");
                    return true;
                }
            }
        }

        Msg.send(p, "§4Bitte benutze: §c/region <remove|list> [<world-name>] [<region-name>]\n§4oder: §c/region <setCube|setRect|setSphere|setCircle> <name> <x1> <y1> [<z1>] <x2> <y2> [<z2>]");
        return false;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (String arg : new String[]{"setCube", "setRect", "setSphere", "setCircle", "list", "remove"}) {
                if (arg.startsWith(search)) {
                    matches.add(arg);
                }
            }

            return matches;
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("remove"))) {
            String search = args[1];
            List<String> matches = new ArrayList<>();

            for (CoreWorld world : CoreSystem.getInstance().getWorldManager().getWorlds()) {
                if (world.getName().startsWith(search)) {
                    matches.add(world.getName());
                }
            }

            if (args[0].equalsIgnoreCase("remove")) {
                for (Region region : CoreSystem.getInstance().getCorePlayer(p).getWorld().getRegions()) {
                    if (region.getName().startsWith(search)) {
                        matches.add(region.getName());
                    }
                }
            }

            return matches;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            String search = args[1];
            List<String> matches = new ArrayList<>();

            for (Region region : CoreSystem.getInstance().getCorePlayer(p).getWorld().getRegions()) {
                if (region.getName().startsWith(search)) {
                    matches.add(region.getName());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }

    private void setRegion(CoreWorld world, Region.Selection selection, String[] args) {
        String name = args[1];
        final int x1 = Integer.parseInt(args[2]), y1, z1, x2, y2, z2;

        if (args.length == 6) {
            z1 = Integer.parseInt(args[3]);
            x2 = Integer.parseInt(args[4]);
            z2 = Integer.parseInt(args[5]);

            y1 = 0;
            y2 = MinecraftServer.getServer().getPropertyManager().getInt("max-build-height", 256);
        } else {
            y1 = Integer.parseInt(args[3]);
            z1 = Integer.parseInt(args[3]);

            x2 = Integer.parseInt(args[5]);
            y2 = Integer.parseInt(args[6]);
            z2 = Integer.parseInt(args[7]);
        }

        world.setRegion(
                new Region(
                        name,
                        selection,
                        new CoreBlockLocation(world, x1, y1, z1),
                        new CoreBlockLocation(world, x2, y2, z2)
                )
        ).save();
    }

}
