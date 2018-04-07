/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
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

public class TpposCMD implements CommandExecutor{

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

			if (p.hasPermission("system.bukkit.tp.pos")) {
				if (commandLabel.equalsIgnoreCase("tppos")) {
					if (args.length == 0) {
						p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze §c/tppos <x> <y> <z>");
						return true;
					}
					Player player = (Player) sender;
					World myworld = player.getWorld();
					Location yourlocation = new Location(myworld, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
					player.teleport(yourlocation);
					sender.sendMessage(CoreSystem.config.getConfigValue("Prefix") + " §7Du wurdest teleportiert!");

				}
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return true;
		}

        return false;
    }
}
