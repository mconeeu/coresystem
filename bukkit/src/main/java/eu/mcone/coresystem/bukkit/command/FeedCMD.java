/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FeedCMD extends CorePlayerCommand {

    public FeedCMD() {
        super("feed", "system.bukkit.feed");
    }

    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.setFoodLevel(40);
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast nun wieder §avolles Essen§2!");
            p.playSound(p.getLocation(), Sound.EAT, 1, 1);
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t != null) {
                t.setFoodLevel(40);
                BukkitCoreSystem.getInstance().getMessenger().send(t, "§2Du hast nun §avolles Essen§2!");
                t.playSound(p.getLocation(), Sound.EAT, 1, 1);
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Dieser Spieler ist nicht online!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/feed [<Spieler>]");
        }

        return false;
    }

}
