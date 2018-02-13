/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCMD implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
			CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

			if (p.hasPermission("system.bukkit.gamemode")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("0")) {
						p.setGameMode(GameMode.SURVIVAL);
						p.setAllowFlight(false);
					} else if (args[0].equalsIgnoreCase("1")) {
						p.setGameMode(GameMode.CREATIVE);
						p.setAllowFlight(true);
					} else if (args[0].equalsIgnoreCase("2")) {
						p.setGameMode(GameMode.ADVENTURE);
						p.setAllowFlight(false);
					} else if (args[0].equalsIgnoreCase("3")) {
						p.setGameMode(GameMode.SPECTATOR);
						p.setAllowFlight(true);
					} else {
						p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze: §c/gm §4oder §c/gamemode <Gamemode>");
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
						return true;
					}

					p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du hast deinen Spielmodus auf §f" + p.getGameMode() + " §2gesetzt!");
					p.playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 1);
					return true;

				} else if (args.length == 2) {
					try {
						Player t = Bukkit.getPlayer(args[1]);

						if (args[0].equalsIgnoreCase("1")) {
							t.setGameMode(GameMode.CREATIVE);
							p.setAllowFlight(true);
						} else if (args[0].equalsIgnoreCase("0")) {
							t.setGameMode(GameMode.SURVIVAL);
							p.setAllowFlight(false);
						} else if (args[0].equalsIgnoreCase("2")) {
							t.setGameMode(GameMode.ADVENTURE);
							p.setAllowFlight(false);
						} else if (args[0].equalsIgnoreCase("3")) {
							t.setGameMode(GameMode.SPECTATOR);
							p.setAllowFlight(true);
						} else {
							p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze: §c/gm §4oder §c/gamemode <Gamemode> [<Spieler>]");
							p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
							return true;
						}

						t.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§7Dein Spielmodus wurde auf §f" + t.getGameMode() + " §7gesetzt.");
						p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du hast den Spielmodus von §f" + t.getName() + " §2auf §a" + t.getGameMode() + "§2 gesetzt.");
						t.playSound(t.getLocation(), Sound.BLAZE_HIT, 1, 1);
						p.playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 1);
						return true;

					} catch (NullPointerException d) {
						p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Spieler ist nicht Online oder existiert nicht!");
						return true;
					}

				} else {
					p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze: §c/gm §4oder §c/gamemode <GamemodeCMD> [<Spieler>]");
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
					return true;
				}
			} else {
				p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Rechte für den Befehl §cgamemode");
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
				return true;
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return true;
		}
	}
}