/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
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
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du hast den §fFlugmodus §2deaktiviert!");
            } else {
                p.setAllowFlight(true);
                p.setFlying(true);
                fly.add(p.getUniqueId());
                BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du hast den §fFlugmodus §2aktiviert!");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/fly");
        }

        return true;
    }
}
