/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("");
            sender.sendMessage("§8§m---------- §r§3§lMCONE-BukkitCoreSystem §8§m----------");
            sender.sendMessage("§8[§7§l!§8] §fSystem §8» §7Entwickelt von §fTwinsterHD §7und §frufi");
            sender.sendMessage("§r");
            sender.sendMessage("§7§oWir bemühen uns darum alle Systeme und Spielmodi so effizient wie möglich zu gestalten.");
            sender.sendMessage("§7§oDeshalb sind auch alle von uns verwendeten Plugins ausschließlich selbst entwickelt!");
            sender.sendMessage("§8§m---------- §r§3§lMCONE-BukkitCoreSystem §8§m----------");
            sender.sendMessage("");
        } else if (args[0].equals("reload")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

                if (!p.hasPermission("system.bukkit.reload")) {
                    p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                    return true;
                }
            }

            if (args.length == 1) {
                sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§aMySQL-Config wird neu geladen...");
                BukkitCoreSystem.config.store();

                sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§aPermissions werden neu geladen...");
                BukkitCoreSystem.getInstance().getPermissionManager().reload();

                sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§aScorebard wird neu geladen...");
                for (BukkitCorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                    cp.getScoreboard().reload(BukkitCoreSystem.getInstance());
                }
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("config")) {
                    sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§aMySQL-Config wird neu geladen...");
                    BukkitCoreSystem.config.store();
                } else if (args[1].equalsIgnoreCase("permissions")) {
                    sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§aPermissions werden neu geladen...");
                    Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                        BukkitCoreSystem.getInstance().getPermissionManager().reload();
                        for (BukkitCorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                            cp.reloadPermissions();
                        }
                    });
                } else if (args[1].equalsIgnoreCase("scoreboard")) {
                    sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§aScorebard wird neu geladen...");
                    for (BukkitCorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                        cp.getScoreboard().reload(BukkitCoreSystem.getInstance());
                    }
                }
            }
        }
        return true;
    }
}
