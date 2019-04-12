/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

public class VanishCMD extends CorePlayerCommand {

    public VanishCMD() {
        super("vanish", "system.bukkit.vanish", "v");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            CorePlayer cp = BukkitCoreSystem.getSystem().getCorePlayer(p);

            if (!cp.isVanished()) {
                cp.setVanished(true);
            } else {
                cp.setVanished(false);
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/vanish");
        }

        return false;
    }

}
