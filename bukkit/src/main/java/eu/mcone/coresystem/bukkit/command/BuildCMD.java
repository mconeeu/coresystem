/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.bukkit.world.BuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildCMD extends CorePlayerCommand {

    private final BuildSystem buildSystem;

    public BuildCMD(BuildSystem buildSystem) {
        super("build", "system.bukkit.build", "b");
        this.buildSystem = buildSystem;
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            buildSystem.changeBuildMode(p);
            return true;
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);

            if (t != null) {
                buildSystem.changeBuildMode(t);
                Msg.send(p, "§2Du hast den Build-Modus von §a" + args[0] + "§2 verändert!");
            } else {
                Msg.send(p, "§4Dieser Spieler ist nicht online!");
            }
            return true;
        }

        Msg.send(p, "§4Bitte benutze: §c/build [<Spieler>]");
        return true;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            List<String> matches = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != p && player.getName().startsWith(search)) {
                    matches.add(player.getName());
                }
            }

            return matches;
        }

        return Collections.emptyList();
    }

}
