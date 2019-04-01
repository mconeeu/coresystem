/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCMD extends CoreCommand {

    public BukkitCMD() {
        super("bukkit");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
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

                if (!p.hasPermission("system.bukkit.reload")) {
                    BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                    return true;
                }
            }

            if (args.length == 1) {
                BukkitCoreSystem.getInstance().getMessager().send(sender, "§aTranslationManager wird neu geladen...");
                BukkitCoreSystem.getInstance().getTranslationManager().reload();

                BukkitCoreSystem.getInstance().getMessager().send(sender, "§aPermissions werden neu geladen...");
                Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                    BukkitCoreSystem.getInstance().getPermissionManager().reload();
                    for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                        cp.reloadPermissions();
                    }
                });

                BukkitCoreSystem.getInstance().getMessager().send(sender, "§aScorebard wird neu geladen...");
                for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                    cp.getScoreboard().reload();
                }
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("tranlsations")) {
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§aTranslationManager wird neu geladen...");
                    BukkitCoreSystem.getInstance().getTranslationManager().reload();
                } else if (args[1].equalsIgnoreCase("permissions")) {
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§aPermissions werden neu geladen...");
                    Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                        BukkitCoreSystem.getInstance().getPermissionManager().reload();
                        for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                            cp.reloadPermissions();
                        }
                    });
                } else if (args[1].equalsIgnoreCase("scoreboard")) {
                    BukkitCoreSystem.getInstance().getMessager().send(sender, "§aScorebard wird neu geladen...");
                    for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                        cp.getScoreboard().reload();
                    }
                }
            }
        }
        return true;
    }
}
