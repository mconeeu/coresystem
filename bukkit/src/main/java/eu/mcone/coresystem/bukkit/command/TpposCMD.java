/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpposCMD extends CoreCommand {

    public TpposCMD() {
        super(CoreSystem.getInstance(), "tppos");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return false;

            if (p.hasPermission("system.bukkit.tp.pos")) {
                if (commandLabel.equalsIgnoreCase("tppos")) {
                    if (args.length == 0) {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/tppos <x> <y> <z>");
                    } else {
                        Player player = (Player) sender;
                        World myworld = player.getWorld();
                        Location yourlocation = new Location(myworld, Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                        player.teleport(yourlocation);
                        BukkitCoreSystem.getInstance().getMessager().send(sender, "§7Du wurdest teleportiert!");
                    }
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
