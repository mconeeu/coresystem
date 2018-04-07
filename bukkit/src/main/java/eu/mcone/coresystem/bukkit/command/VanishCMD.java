/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class VanishCMD implements CommandExecutor{

	private static ArrayList<UUID> vanish = new ArrayList<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

			if (p.hasPermission("system.bukkit.vanish")) {
				if (args.length == 0) {
					if (!vanish.contains(p.getUniqueId())) {
						vanish.add(p.getUniqueId());
						for (Player all : Bukkit.getOnlinePlayers()) {
							all.hidePlayer(p);
						}
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
						p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du bist nun im §fVanish §2Modus!");

						return true;
					} else {
						vanish.remove(p.getUniqueId());
						for (Player all : Bukkit.getOnlinePlayers()) {
							all.showPlayer(p);
						}
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
						p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du bist nun nicht mehr im §fVanish §2Modus!");

						return true;
					}
				} else {
					p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze §c/vanish");
				}
			} else {
				p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return true;
		}
		return false;
	}

}
