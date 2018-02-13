/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (cmd.getName().equalsIgnoreCase("tp")) {
                if (args.length == 1) {
                    if (p.hasPermission("system.bukkit.tp")) {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        if (target == null) {
                            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                            return true;
                        }
                        p.teleport(target.getLocation());
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du wirst zu §f" + args[0] + "§2 teleportiert!");
                        return true;
                    } else {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                    }
                } else if (args.length == 2) {
                    if (p.hasPermission("system.bukkit.tp.others")) {
                        Player target1 = Bukkit.getServer().getPlayer(args[0]);
                        Player target2 = Bukkit.getServer().getPlayer(args[1]);
                        if (target1 == null) {
                            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Spieler §f" + args[0] + " §4konnte nicht gefunden werden!");
                            return true;
                        } else if (target2 == null) {
                            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Spieler §f" + args[1] + " §4konnte nicht gefunden werden!");
                            return true;
                        }
                        target1.teleport(target2.getLocation());
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Der Spieler §f" + args[0] + "§2 wird zu §f" + args[1] + "§2 teleportiert!");
                        return true;
                    } else {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                    }
                } else if (args.length == 3) {
                    if (p.hasPermission("system.bukkit.tp.pos")) {
                        World tpworld = p.getWorld();
                        Location tplocation = new Location(tpworld, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                        p.teleport(tplocation);
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du wirst zu §f" + args[0] + " " + args[1] + " " + args[2] + "§2 teleportiert!");
                    } else {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                    }
                } else {
                    p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze §c/tp [<Spieler>] <Zielspieler> §4oder §c/tp <x> <y> <z>");
                    return true;
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }
               
		return false;
	}
}
