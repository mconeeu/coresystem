/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FeedCMD extends CorePlayerCommand {

    public FeedCMD() {
        super("feed", "system.bukkit.feed");
    }

    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.setFoodLevel(40);
            Msg.send(p, "§2Du hast nun wieder §avolles Essen§2!");
            Sound.play(p, org.bukkit.Sound.EAT);
        } else if (args.length == 1) {
            Player t = Bukkit.getPlayer(args[0]);
            if (t != null) {
                t.setFoodLevel(40);
                Msg.send(t, "§2Du hast nun §avolles Essen§2!");
                Sound.play(p, org.bukkit.Sound.EAT);
            } else {
                Msg.send(p, "§4Dieser Spieler ist nicht online!");
            }
        } else {
            Msg.send(p, "§4Bitte benutze: §c/feed [<Spieler>]");
        }

        return false;
    }

    @Override
    public List<String> onPlayerTabComplete(Player p, String[] args) {
        String search = args[0];
        List<String> matches = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != p && player.getName().startsWith(search)) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

}
