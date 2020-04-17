/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EnderchestCMD extends CorePlayerCommand {

    public EnderchestCMD() {
        super("enderchest", "system.bukkit.ecsee.self", "ec", "ecsee");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.openInventory(p.getEnderChest());

            if (p.hasPermission("system.bukkit.ecsee.other")) {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§f§oTipp: §7Benutze §f/ec <player>§7 um die Enderkiste eines anderen Spielers zu sehen!");
            }
        } else if (args.length == 1) {
            if (p.hasPermission("system.bukkit.ecsee.other")) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    p.openInventory(t.getEnderChest());
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Der Spieler §c" + args[0] + "§4 ist nicht online!");
                }
            } else {
                CoreSystem.getInstance().getMessenger().sendTransl(p, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p,
                    p.hasPermission("system.bukkit.ecsee.other") ? "§4Bitte benutze: §c/ec [<player>]" : "§4Bitte benutze: §c/ec"
            );
        }

        return true;
    }

}
