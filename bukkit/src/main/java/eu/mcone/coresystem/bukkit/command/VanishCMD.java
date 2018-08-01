/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class VanishCMD extends CoreCommand {

    private static ArrayList<UUID> vanish = new ArrayList<>();

    public VanishCMD() {
        super(CoreSystem.getInstance(), "vanish");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return false;

            if (p.hasPermission("system.bukkit.vanish")) {
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
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return false;
    }

}
