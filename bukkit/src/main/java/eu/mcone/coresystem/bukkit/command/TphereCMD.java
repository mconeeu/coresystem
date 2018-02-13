/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCMD implements CommandExecutor{

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (p.hasPermission("system.bukkit.tp.others")) {
                if (cmd.getName().equalsIgnoreCase("tphere")) {
                    if (args.length == 0) {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze §c/tphere <Spieler>");
                        return true;
                    }

                    Player target = Bukkit.getServer().getPlayer(args[0]);
                    if (target == null) {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Spieler §f" + args[0] + "§4 konnte nicht gefunden werden!");
                        return true;
                    }
                    target.teleport(p.getLocation());
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
