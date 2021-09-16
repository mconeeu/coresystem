/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClearCMD extends CoreCommand {

    public ClearCMD() {
        super("clear", "system.bukkit.clear");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().clear();
                Msg.send(sender, "§2Du hast dein Inventar erfolgreich geleert!");
            } else {
                Msg.sendTransl(sender, "system.command.consolesender");
            }
            return true;
        } else if (args.length == 1) {
            if (sender.hasPermission("system.bukkit.clear.other")) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    t.getInventory().clear();
                    Msg.send(sender, "§2Du hast erfolgreich das Inventar von §a" + t.getName() + "§2 geleert!");
                } else {
                    Msg.send(sender, "§4Der Spieler §c" + args[0] + "§4 ist nicht online!");
                }
            } else {
                Msg.sendTransl(sender, "system.command.noperm");
            }
            return true;
        }

        Msg.send(sender, "§4Bitte benutze: §c/clear " + (sender.hasPermission("system.bukkit.clear.other") ? "[<player>]" : ""));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("system.bukkit.clear.other") && args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != sender && player.getName().startsWith(search)) {
                    matches.add(player.getName());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }
}
