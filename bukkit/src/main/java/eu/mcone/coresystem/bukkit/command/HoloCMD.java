/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.hologram.HologramManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoloCMD implements CommandExecutor{

    private HologramManager api;

    public HoloCMD(HologramManager api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!BukkitCoreSystem.getInstance().getCooldownSystem().addAndCheck(BukkitCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return false;

            if (p.hasPermission("system.bukkit.holo")) {
                if (args.length >= 3) {
                    if (args[0].equalsIgnoreCase("add")) {
                        StringBuilder line = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            line.append(args[i]);
                            if (i < args.length-1) line.append(" ");
                        }

                        api.addHologram(args[1], p.getLocation(), line.toString());
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hologramm §f" + args[1] + "§2 erfolgreich hinzugefügt!");
                        return true;
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        api.removeHologram(args[1]);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hologramm §f" + args[1] + "§2 erfolgreich gelöscht!");
                        return true;
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        StringBuilder result = new StringBuilder();
                        result.append(BukkitCoreSystem.getInstance().getTranslationManager().get("system.prefix.server")).append("§7Diese Hologramme sind gerade geladen:\n");
                        int i = api.getHolograms().keySet().size();
                        for (String h : api.getHolograms().keySet()) {
                            result.append("§3§o").append(h);
                            if (i <= 1) continue;
                            result.append("§7, ");
                            i--;
                        }
                        p.sendMessage(result.toString());
                        return true;
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        api.reload();
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Hologramme erfolgreich neu geladen!");
                        return true;
                    }
                }

                BukkitCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/holo <add | remove | list | reload> [<Name>] [<Erste Zeile>]");
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        return true;
    }

}
