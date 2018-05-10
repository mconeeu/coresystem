/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.world.LocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCMD implements CommandExecutor {

    private LocationManager locationManager;

    public SpawnCMD(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

            if (args.length == 0 && locationManager.isAllowSpawnCMD()) {
                locationManager.teleport(p, "spawn");
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("system.bukkit.setspawn")) {
                    if (locationManager.putLocation(args[1], p.getLocation())) {
                        Messager.send(p, "§2Die Location §f"+args[1]+"§2 wurde erfolgreich gesetzt!");
                    } else {
                        Messager.send(p, "§4Die Location §c"+args[1]+"§4 wurde vom Plugin nicht registriert und kann daher nicht gesetzt werden!");
                    }
                } else {
                    Messager.send(p, "§4Du hast keine Berechtigung für diesen Befehl!");
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                locationManager.downloadLocations();
                Messager.send(p, "§2Der LocationManager wurde erfolgreich neu geladen!");
            }

            Messager.send(p, "§4Benutze §c/spawn §4um dich zum Spawn zu teleportieren");
        } else {
            Messager.sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }
}
