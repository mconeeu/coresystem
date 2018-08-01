/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCMD extends CoreCommand {

    public TpCMD() {
        super(CoreSystem.getInstance(), "tp");
    }

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

            if (cmd.getName().equalsIgnoreCase("tp")) {
                if (args.length == 1) {
                    if (p.hasPermission("system.bukkit.tp")) {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        if (target == null) {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                            return true;
                        }
                        p.teleport(target.getLocation());
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wirst zu §f" + args[0] + "§2 teleportiert!");
                        return true;
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                    }
                } else if (args.length == 2) {
                    if (p.hasPermission("system.bukkit.tp.others")) {
                        Player target1 = Bukkit.getServer().getPlayer(args[0]);
                        Player target2 = Bukkit.getServer().getPlayer(args[1]);

                        if (target1 == null) {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                        } else if (target2 == null) {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[1] + " §4konnte nicht gefunden werden!");
                        } else {
                            target1.teleport(target2.getLocation());
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Der Spieler §f" + args[0] + "§2 wird zu §f" + args[1] + "§2 teleportiert!");
                        }
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                    }
                } else if (args.length == 3) {
                    if (p.hasPermission("system.bukkit.tp.pos")) {
                        World tpworld = p.getWorld();
                        Location tplocation = new Location(tpworld, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                        p.teleport(tplocation);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wirst zu §f" + args[0] + " " + args[1] + " " + args[2] + "§2 teleportiert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/tp [<Spieler>] <Zielspieler> §4oder §c/tp <x> <y> <z>");
                }
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }
               
		return true;
	}
}
