/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.util.BuildSystem;
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

            if (args.length == 0) {
                buildSystem.changeBuildMode(p);
                return true;
            } else if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    buildSystem.changeBuildMode(t);
                } else {
                    p.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Dieser Spieler ist nicht online!");
                }
                return true;
            }
        }

        sender.sendMessage(BukkitCoreSystem.config.getConfigValue("Prefix") + "§4Bitte benutze: §c/build [<Spieler>]");
        return false;
    }

}
