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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlyCMD extends CoreCommand {

    private static List<UUID> fly = new ArrayList<>();

    public FlyCMD() {
        super(CoreSystem.getInstance(), "fly");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

            if (p.hasPermission("system.bukkit.fly")) {
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
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
