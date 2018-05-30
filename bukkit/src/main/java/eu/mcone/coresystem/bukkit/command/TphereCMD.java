/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return false;

            if (p.hasPermission("system.bukkit.tp.others")) {
                if (cmd.getName().equalsIgnoreCase("tphere")) {
                    if (args.length == 0) {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/tphere <Spieler>");
                    } else {
                        Player target = Bukkit.getServer().getPlayer(args[0]);
                        if (target == null) {
                            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[0] + "§4 konnte nicht gefunden werden!");
                        } else {
                            target.teleport(p.getLocation());
                        }
                    }
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
