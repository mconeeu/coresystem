/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishCMD extends CorePlayerCommand {

    public VanishCMD() {
        super("vanish", "system.bukkit.vanish", "v");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            setVanished(BukkitCoreSystem.getSystem().getCorePlayer(p));
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                setVanished(BukkitCoreSystem.getSystem().getCorePlayer(t));
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Der Vanish Modus von §a"+t.getName()+"§2 wurde geändert!");
            } else {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler ist nicht online!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/vanish [<name>]");
        }

        return false;
    }

    private static void setVanished(CorePlayer cp) {
        if (!cp.isVanished()) {
            cp.setVanished(true);
        } else {
            cp.setVanished(false);
        }
    }

}
