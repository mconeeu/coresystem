/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.world.BuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCMD implements CommandExecutor {

    private BuildSystem buildSystem;

    public BuildCMD(BuildSystem buildSystem) {
        this.buildSystem = buildSystem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.hasPermission("system.bukkit.build")) {
                if (args.length == 0) {
                    buildSystem.changeBuildMode(p);
                    return true;
                } else if (args.length == 1) {
                    Player t = Bukkit.getPlayer(args[0]);

                    if (t != null) {
                        buildSystem.changeBuildMode(t);
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du hast den Build-Modus von §a"+args[0]+"§2 verändert!");
                    } else {
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§4Dieser Spieler ist nicht online!");
                    }
                    return true;
                }
            } else {
                BukkitCoreSystem.getInstance().getMessager().sendTransl(p, "system.command.noperm");
                return true;
            }
        } else {
            BukkitCoreSystem.getInstance().getMessager().sendTransl(sender, "system.command.consolesender");
        }

        BukkitCoreSystem.getInstance().getMessager().send(sender, "§4Bitte benutze: §c/build [<Spieler>]");
        return true;
    }

}
