/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.StatsInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCMD implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

			if (BukkitCoreSystem.cfg.getConfig().getBoolean("StatsAPI")) {
				if (cmd.getName().equalsIgnoreCase("stats")) {
					new StatsInventory(p);
				}

				return true;
			} else {
				p.closeInventory();
				p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§cDiese Funktion ist momentan deaktiviert");
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return true;
		}

		return false;
	}
	
}
