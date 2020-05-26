/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InvCMD extends CorePlayerCommand {

    public InvCMD() {
        super("inv", "system.bukkit.invsee.other", "invsee");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                if (p != t) {
                    p.openInventory(t.getInventory());
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§7Drücke §fE §7um dein eigenes Inventar zu öffnen!");
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Der Spieler §c" + args[0] + "§4 ist nicht online!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p,
                    p.hasPermission("system.bukkit.invsee.other") ? "§4Bitte benutze: §c/inv <player>" : "§4Bitte benutze: §c/inv"
            );
        }

        return true;
    }

}
