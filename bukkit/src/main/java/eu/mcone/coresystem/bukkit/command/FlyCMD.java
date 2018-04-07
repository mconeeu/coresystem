/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
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
            if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return true;
            CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

            if (p.hasPermission("system.bukkit.fly")) {
                if (args.length == 0) {
                    if (fly.contains(p.getUniqueId())) {
                        p.setAllowFlight(false);
                        fly.remove(p.getUniqueId());
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du hast den §fFlugmodus §2deaktiviert!");
                    } else {
                        p.setAllowFlight(true);
                        fly.add(p.getUniqueId());
                        p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du hast den §fFlugmodus §2aktiviert!");
                    }
                } else {
                    p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze: §c/fly");
                }
            } else {
                p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }
        return false;
    }
}
