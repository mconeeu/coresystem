/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.util.LocationManager;
import org.bukkit.Bukkit;
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
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (args.length == 0) {
                locationManager.teleport(p, "spawn");
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("system.bukkit.setspawn")) {
                    if (locationManager.putLocation(args[1], p.getLocation())) {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Die Location §f"+args[1]+"§2 wurde erfolgreich gesetzt!");
                    } else {
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Die Location §c"+args[1]+"§4 wurde vom Plugin nicht registriert und kann daher nicht gesetzt werden!");
                    }
                } else {
                    p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                locationManager.downloadLocations();
                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Der LocationManager wurde erfolgreich neu geladen!");
            }

            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Benutze §c/spawn §4um dich zum Spawn zu teleportieren");
        } else {
            Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
        }
        return true;
    }
}
