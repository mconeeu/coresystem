/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpposCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return false;

            if (p.hasPermission("system.bukkit.tp.pos")) {
                if (commandLabel.equalsIgnoreCase("tppos")) {
                    if (args.length == 0) {
                        Messager.send(p, "§4Bitte benutze §c/tppos <x> <y> <z>");
                    } else {
                        Player player = (Player) sender;
                        World myworld = player.getWorld();
                        Location yourlocation = new Location(myworld, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                        player.teleport(yourlocation);
                        Messager.send(sender, "§7Du wurdest teleportiert!");
                    }
                }
            } else {
                Messager.sendTransl(p, "system.command.noperm");
            }
        } else {
            Messager.sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
