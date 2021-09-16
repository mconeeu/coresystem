/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.bukkit.inventory.profile.CoreProfileInventory;
import eu.mcone.coresystem.bukkit.inventory.profile.CoreProfilePlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProfileCMD extends CorePlayerCommand {

    public ProfileCMD() {
        super("profile", null, "profil", "p");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            new CoreProfileInventory(p);
            Sound.click(p);
            return true;
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                if (t == p) {
                    new CoreProfileInventory(p);
                } else {
                    new CoreProfilePlayerInventory(p, t);
                }

                Sound.click(p);
                return true;
            }
        }

        Msg.sendError(p, "Bitte benutze: §c/profil [<name>]");
        Sound.error(p);
        return false;
    }
    
}
