/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class VanishCMD extends CorePlayerCommand {

    private static ArrayList<UUID> vanish = new ArrayList<>();

    public VanishCMD() {
        super("vanish", "system.bukkit.vanish", "v");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            if (!vanish.contains(p.getUniqueId())) {
                vanish.add(p.getUniqueId());
                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.hidePlayer(p);
                }
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du bist nun im §fVanish §2Modus!");
            } else {
                vanish.remove(p.getUniqueId());
                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.showPlayer(p);
                }
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du bist nun nicht mehr im §fVanish §2Modus!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/vanish");
        }

        return false;
    }

}
