/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCMD extends CoreCommand {

    public ClearCMD() {
        super("clear", "system.bukkit.clear");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().clear();
                CoreSystem.getInstance().getMessager().send(sender, "§2Du hast dein Inventar erfolgreich geleert!");
            } else {
                CoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
            }
            return true;
        } else if (args.length == 1) {
            if (sender.hasPermission("system.bukkit.clear.other")) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    t.getInventory().clear();
                    CoreSystem.getInstance().getMessager().send(sender, "§2Du hast erfolgreich das Inventar von §a"+t.getName()+"§2 geleert!");
                } else {
                    CoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler §c"+args[0]+"§4 ist nicht online!");
                }
            } else {
                CoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.noperm");
            }
            return true;
        }

        CoreSystem.getInstance().getMessager().send(sender, "§4Bitte benutze: §c/clear " + (sender.hasPermission("system.bukkit.clear.other") ? "[<player>]" : ""));
        return false;
    }
}
