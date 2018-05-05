/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.ProfileInventory;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

            if (args.length == 0) {
                new ProfileInventory(p);
                p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
            }
        } else {
            Messager.sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
