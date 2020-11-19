/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealCMD extends CorePlayerCommand {

    public HealCMD() {
        super("heal", "system.bukkit.heal");
    }

    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast nun wieder §avolles Leben§3!");
            Sound.play(p, org.bukkit.Sound.EAT);
            p.setFireTicks(1);
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t != null) {
                t.setHealth(20.0D);
                t.setFoodLevel(20);
                BukkitCoreSystem.getInstance().getMessenger().send(t, "§2Du hast nun §avolles Leben§3!");
                Sound.play(t, org.bukkit.Sound.EAT);
                t.setFireTicks(1);
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Dieser Spieler ist nicht online!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/heal [<Spieler>]");
        }

        return false;
    }
}
