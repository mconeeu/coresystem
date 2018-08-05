/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TphereCMD extends CorePlayerCommand {

    public TphereCMD() {
        super("tphere", "system.bukkit.tp.others");
    }

    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/tphere <Spieler>");
        } else {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §f" + args[0] + "§4 konnte nicht gefunden werden!");
            } else {
                target.teleport(p.getLocation());
            }
        }

        return true;
    }
}
