/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCMD extends CorePlayerCommand {

    private static List<UUID> fly = new ArrayList<>();

    public FlyCMD() {
        super("fly", "system.bukkit.fly");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            if (fly.contains(p.getUniqueId())) {
                p.setAllowFlight(false);
                p.setFlying(false);
                fly.remove(p.getUniqueId());
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast den §fFlugmodus §2deaktiviert!");
            } else {
                p.setAllowFlight(true);
                p.setFlying(true);
                fly.add(p.getUniqueId());
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast den §fFlugmodus §2aktiviert!");
            }
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if (fly.contains(target.getUniqueId())) {
                    target.setAllowFlight(false);
                    target.setFlying(false);
                    fly.remove(target.getUniqueId());
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast den §fFlugmodus §2für §f" + target.getName() + " §2deaktiviert!");
                } else {
                    target.setAllowFlight(true);
                    target.setFlying(true);
                    fly.add(target.getUniqueId());
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast den §fFlugmodus §2für §f" + target.getName() + " §2aktiviert!");
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Der Spieler §c" + args[0] + " §4konnte nicht gefunden werden");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/fly §4oder §c/fly {spieler}");
        }

        return true;
    }
}
