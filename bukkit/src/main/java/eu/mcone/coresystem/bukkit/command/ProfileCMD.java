/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.ProfileInventory;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCMD extends CoreCommand {

    public ProfileCMD() {
        super(CoreSystem.getInstance(), "profile");
    }

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
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
