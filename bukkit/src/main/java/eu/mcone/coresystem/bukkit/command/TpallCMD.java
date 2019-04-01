/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpallCMD extends CorePlayerCommand {

    public TpallCMD() {
        super("tpall", "system.bukkit.tp.all");
    }

    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            for (Player p1 : Bukkit.getOnlinePlayers()) {
                p1.teleport(p.getLocation());
            }
        } else if (args.length == 1) {
            for (Player p1 : Bukkit.getOnlinePlayers()) {
                Player target = Bukkit.getServer().getPlayer(args[0]);

                if (target != null) {
                    p1.teleport(target.getLocation());
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[0] + "§4 konnte nicht gefunden werden!");
                }
            }
        }

        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/tpall <Spieler>");

        return true;
    }
}