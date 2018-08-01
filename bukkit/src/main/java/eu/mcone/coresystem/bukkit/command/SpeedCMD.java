/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCMD extends CoreCommand {

    public SpeedCMD() {
        super(CoreSystem.getInstance(), "speed");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 1) {
                float speed = Float.valueOf(args[0]);

                if (speed > -1 && speed < 1) {
                    if (p.isFlying()) {
                        p.setFlySpeed(speed);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Deine Fluggeschwindigkeit wurde auf §f" + speed + "§2 gesetzt!");
                    } else {
                        p.setWalkSpeed(speed);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Deine Laufgeschwindigkeit wurde auf §f" + speed + "§2 gesetzt!");
                    }
                } else {
                    BukkitCoreSystem.getInstance().getMessager().send(p, "§4Du kannst nur Geschwindigkeiten zwischen -1 und 1 setzen!");
                }
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }

}
