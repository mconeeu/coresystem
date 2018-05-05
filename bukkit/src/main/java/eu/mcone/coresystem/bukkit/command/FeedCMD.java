/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCMD implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args){
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

			if (p.hasPermission("system.bukkit.feed")) {
				if (args.length == 0) {
					p.setFoodLevel(40);
					Messager.send(p, "§2Du hast nun wieder §avolles Essen§2!");
					p.playSound(p.getLocation(), Sound.EAT, 1, 1);
				} else if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					if (t != null) {
						t.setFoodLevel(40);
						Messager.send(t, "§2Du hast nun §avolles Essen§2!");
						t.playSound(p.getLocation(), Sound.EAT, 1, 1);
					} else {
						Messager.send(p, "§4Dieser Spieler ist nicht online!");
					}
				} else {
					Messager.send(p, "§4Bitte benutze: §c/feed [<Spieler>]");
                }
			} else {
				Messager.sendTransl(p, "system.command.noperm");
			}
	    } else {
			Messager.sendTransl(sender, "system.command.consolesender");
			return true;
		}
		return false;
	 }
}
