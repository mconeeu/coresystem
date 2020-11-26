/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TpCMD extends CoreCommand {

    public TpCMD() {
        super("tp", "system.bukkit.tp");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    p.teleport(t.getLocation());
                    BukkitCoreSystem.getInstance().getMessenger().sendSuccess(p, "§2Du wirst zu ![" + t.getName() + "] teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().sendError(p, "Der Spieler ![" + args[0] + "] konnte nicht gefunden werden!");
                }

                return true;
            } else {
                CoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.consolesender");
                return false;
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("system.bukkit.tp.others")) {
                Player t1 = Bukkit.getServer().getPlayer(args[0]);
                Player t2 = Bukkit.getServer().getPlayer(args[1]);

                if (t1 == null) {
                    BukkitCoreSystem.getInstance().getMessenger().sendError(sender, "Der Spieler ![" + args[0] + "] konnte nicht gefunden werden!");
                } else if (t2 == null) {
                    BukkitCoreSystem.getInstance().getMessenger().sendError(sender, "Der Spieler ![" + args[1] + "] konnte nicht gefunden werden!");
                } else {
                    t1.teleport(t2.getLocation());
                    BukkitCoreSystem.getInstance().getMessenger().sendSuccess(sender, "Der Spieler ![" + t1.getName() + "] wird zu +[" + t2.getName() + "] teleportiert!");
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.noperm");
            }
        } else if (args.length == 3) {
            if (sender.hasPermission("system.bukkit.tp.pos")) {
                if (sender instanceof Player) {
                    try {
                        Player p = (Player) sender;
                        Location loc = getTpCoords(p.getLocation(), args, 0);

                        p.teleport(loc);
                        BukkitCoreSystem.getInstance().getMessenger().sendSuccess(p, "Du wirst zu ![" + (int) loc.getX() + " " + (int) loc.getY() + " " + (int) loc.getZ() + "] teleportiert!");
                    } catch (NumberFormatException e) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§4Bitte benutze nur Zahlen oder ~ als Koordinaten");
                    }
                } else {
                    CoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.consolesender");
                    return false;
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.noperm");
            }
        } else if (args.length == 4) {
            if (sender.hasPermission("system.bukkit.tp.others.pos")) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    Location loc = getTpCoords(
                            sender instanceof Player ? ((Player) sender).getLocation() : t.getLocation(),
                            args,
                            1
                    );

                    t.teleport(loc);
                    BukkitCoreSystem.getInstance().getMessenger().sendSuccess(sender, "Du hast ![" + t.getName() + "] zu +[" + (int) loc.getX() + " " + (int) loc.getY() + " " + (int) loc.getZ() + "] teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().sendError(sender, "Der Spieler ![" + args[0] + "] konnte nicht gefunden werden!");
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(sender, "Bitte benutze §c/tp [<Spieler>] <Zielspieler> §4oder §c/tp <x> <y> <z>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 || (args.length == 2 && !NumberUtils.isNumber(args[0]))) {
            String search = args[args.length-1];
            List<String> matches = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != sender && player.getName().startsWith(search)) {
                    matches.add(player.getName());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }

    private Location getTpCoords(Location currentLocation, String[] args, int start) {
        double[] result = new double[]{currentLocation.getX(), currentLocation.getY(), currentLocation.getZ()};

        for (int i = start, x = 0; x < 3; i++, x++) {
            String arg = args[i];

            if (!arg.equals("~")) {
                if (arg.contains("~")) {
                    result[x] += Double.parseDouble(args[i].replace("~", ""));
                } else {
                    result[x] = Double.parseDouble(args[i]);
                }
            }
        }

        return new Location(currentLocation.getWorld(), result[0], result[1], result[2], currentLocation.getYaw(), currentLocation.getPitch());
    }

}
