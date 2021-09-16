/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

public class SpeedCMD extends CorePlayerCommand {

    public SpeedCMD() {
        super("speed", "system.bukkit.speed");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            float speed = Float.parseFloat(args[0]);

            if (speed > -1 && speed < 1) {
                if (p.isFlying()) {
                    p.setFlySpeed(speed);
                    Msg.send(p, "§2Deine Fluggeschwindigkeit wurde auf §f" + speed + "§2 gesetzt!");
                } else {
                    p.setWalkSpeed(speed);
                    Msg.send(p, "§2Deine Laufgeschwindigkeit wurde auf §f" + speed + "§2 gesetzt!");
                }
            } else {
                Msg.send(p, "§4Du kannst nur Geschwindigkeiten zwischen -1 und 1 setzen!");
            }
            return true;
        }

        Msg.sendError(p, "Bitte benutze: ![/speed <-1..1>]");
        return true;
    }

}
