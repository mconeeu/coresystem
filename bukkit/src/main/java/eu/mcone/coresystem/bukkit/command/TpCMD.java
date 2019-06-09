/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCMD extends CoreCommand {

    public TpCMD() {
        super("tp", "system.bukkit.tp");
    }

    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

                Player target = Bukkit.getServer().getPlayer(args[0]);
                if (target == null) {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                    return true;
                }
                p.teleport(target.getLocation());
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wirst zu §f" + args[0] + "§2 teleportiert!");
                return true;
            } else {
                CoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
                return false;
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("system.bukkit.tp.others")) {
                Player target1 = Bukkit.getServer().getPlayer(args[0]);
                Player target2 = Bukkit.getServer().getPlayer(args[1]);

                if (target1 == null) {
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                } else if (target2 == null) {
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler §f" + args[1] + " §4konnte nicht gefunden werden!");
                } else {
                    target1.teleport(target2.getLocation());
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§2Der Spieler §f" + args[0] + "§2 wird zu §f" + args[1] + "§2 teleportiert!");
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.noperm");
            }
        } else if (args.length == 3) {
            if (sender.hasPermission("system.bukkit.tp.pos")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    World tpworld = p.getWorld();
                    double x = p.getLocation().getX(), y = p.getLocation().getY(), z = p.getLocation().getZ();

                    for (int i = 0; i < 3; i++) {
                        if (!args[i].equals("~")) {
                            switch (i) {
                                case 0:
                                    x = Double.parseDouble(args[i]);
                                case 1:
                                    y = p.getLocation().getY();
                                case 2:
                                    z = p.getLocation().getZ();
                            }
                        }
                    }

                    Location tplocation = new Location(tpworld, x, y, z);

                    p.teleport(tplocation);
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wirst zu §f" + args[0] + " " + args[1] + " " + args[2] + "§2 teleportiert!");
                } else {
                    CoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
                    return false;
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.noperm");
            }
        } else if (args.length == 4) {
            if (sender.hasPermission("system.bukkit.tp.others.pos")) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    World tpworld = sender instanceof Player ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0);
                    double x = sender instanceof Player ? ((Player) sender).getLocation().getX() : 0,
                            y = sender instanceof Player ? ((Player) sender).getLocation().getY() : 0,
                            z = sender instanceof Player ? ((Player) sender).getLocation().getZ() : 0;

                    for (int i = 1; i < 4; i++) {
                        if (!args[i].equals("~")) {
                            switch (i) {
                                case 1:
                                    x = Double.parseDouble(args[i]);
                                case 2:
                                    y = Double.parseDouble(args[i]);
                                case 3:
                                    z = Double.parseDouble(args[i]);
                            }
                        }
                    }

                    Location tplocation = new Location(tpworld, x, y, z);

                    t.teleport(tplocation);
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§2Du hast §a"+args[0]+"§2 zu §f" + args[0] + " " + args[1] + " " + args[2] + "§2 teleportiert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(sender, "§4Bitte benutze §c/tp [<Spieler>] <Zielspieler> §4oder §c/tp <x> <y> <z>");
        }

        return true;
    }

}
