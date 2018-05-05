/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCMD implements CommandExecutor {

    private static List<UUID> fly = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

            if (p.hasPermission("system.bukkit.fly")) {
                if (args.length == 0) {
                    if (fly.contains(p.getUniqueId())) {
                        p.setAllowFlight(false);
                        fly.remove(p.getUniqueId());
                        Messager.send(p, "§2Du hast den §fFlugmodus §2deaktiviert!");
                    } else {
                        p.setAllowFlight(true);
                        fly.add(p.getUniqueId());
                        Messager.send(p, "§2Du hast den §fFlugmodus §2aktiviert!");
                    }
                } else {
                    Messager.send(p, "§4Bitte benutze: §c/fly");
                }
            } else {
                Messager.sendTransl(p, "system.command.noperm");
            }
        } else {
            Messager.sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
