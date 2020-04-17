/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.command.CommandSender;

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
            if (sender.hasPermission("system.bukkit.reload")) {

                if (args.length == 1) {
                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aPermissions werden neu geladen...");
                    BukkitCoreSystem.getInstance().getPermissionManager().reload();
                    for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                        cp.reloadPermissions();
                    }

                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aWorldManager wird neu geladen...");
                    BukkitCoreSystem.getInstance().getWorldManager().reload();

                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aTranslationManager wird neu geladen...");
                    BukkitCoreSystem.getInstance().getTranslationManager().reload();

                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aScorebard wird neu geladen...");
                    for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                        cp.getScoreboard().reload();
                    }

                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aNpcManager wird neu geladen...");
                    BukkitCoreSystem.getInstance().getNpcManager().reload();

                    BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aHologramManager wird neu geladen...");
                    BukkitCoreSystem.getInstance().getHologramManager().reload();
                    return true;
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("permissions")) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aPermissions werden neu geladen...");
                        BukkitCoreSystem.getInstance().getPermissionManager().reload();
                        for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                            cp.reloadPermissions();
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("worlds")) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aWorldManager wird neu geladen...");
                        BukkitCoreSystem.getInstance().getWorldManager().reload();
                        return true;
                    } else if (args[1].equalsIgnoreCase("tranlsations")) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aTranslationManager wird neu geladen...");
                        BukkitCoreSystem.getInstance().getTranslationManager().reload();
                        return true;
                    } else if (args[1].equalsIgnoreCase("scoreboard")) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aScorebard wird neu geladen...");
                        for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                            cp.getScoreboard().reload();
                        }
                        return true;
                    } else if (args[1].equalsIgnoreCase("npcs")) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aNpcManager wird neu geladen...");
                        BukkitCoreSystem.getInstance().getNpcManager().reload();
                        return true;
                    } else if (args[1].equalsIgnoreCase("holograms")) {
                        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§aHologramManager wird neu geladen...");
                        BukkitCoreSystem.getInstance().getHologramManager().reload();
                        return true;
                    }
                }
            } else {
                BukkitCoreSystem.getInstance().getMessenger().sendTransl(sender, "system.command.noperm");
                return false;
            }
        }

        BukkitCoreSystem.getInstance().getMessenger().send(sender, "§4Bitte benutze: §c/bukkit [reload] [<permissions | worlds | translations | scoreboard | npcs | holograms>]");
        return false;
    }
}
