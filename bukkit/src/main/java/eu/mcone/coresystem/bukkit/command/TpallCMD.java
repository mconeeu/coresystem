/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpallCMD implements CommandExecutor{

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (p.hasPermission("system.bukkit.tp.all")) {
                if (cmd.getName().equalsIgnoreCase("tpall")) {
                    if (args.length == 0) {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze §c/tpall <Spieler>");
                        return true;
                    }

                    for (Player p1 : Bukkit.getOnlinePlayers()) {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        p1.teleport(target.getLocation());

                        if (target == null) {
                            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Spieler §f" + args[0] + "§4 konnte nicht gefunden werden!");
                            return true;
                        }
                    }
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