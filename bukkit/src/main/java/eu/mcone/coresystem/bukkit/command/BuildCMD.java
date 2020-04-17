/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.world.BuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BuildCMD extends CorePlayerCommand {

    private BuildSystem buildSystem;

    public BuildCMD(BuildSystem buildSystem) {
        super("build", "system.bukkit.build", "b");
        this.buildSystem = buildSystem;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (p.hasPermission("system.bukkit.build")) {
            if (args.length == 0) {
                buildSystem.changeBuildMode(p);
                return true;
            } else if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t != null) {
                    buildSystem.changeBuildMode(t);
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du hast den Build-Modus von §a" + args[0] + "§2 verändert!");
                } else {
                    BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Dieser Spieler ist nicht online!");
                }
                return true;
            }
        } else {
            BukkitCoreSystem.getInstance().getMessenger().sendTransl(p, "system.command.noperm");
            return true;
        }

        BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/build [<Spieler>]");
        return true;
    }

}
