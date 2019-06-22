/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.inventory.ProfileInventory;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ProfileCMD extends CorePlayerCommand {

    public ProfileCMD() {
        super("profile", null, "profil", "p");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            new ProfileInventory(p).openInventory();
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        }

        return true;
    }
}
