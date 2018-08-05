/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TpposCMD extends CorePlayerCommand {

    public TpposCMD() {
        super("tppos", "system.bukkit.tp.pos");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/tppos <x> <y> <z>");
        } else {
            World myworld = p.getWorld();
            Location yourlocation = new Location(myworld, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            p.teleport(yourlocation);
            BukkitCoreSystem.getInstance().getMessager().send(p, "§7Du wurdest teleportiert!");
        }

        return true;
    }
}
