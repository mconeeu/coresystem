/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.util.Random;
import org.bukkit.scoreboard.Team;

public class MainScoreboard extends CoreScoreboard {

    @Override
    public void setPlayerTeams(BukkitCorePlayer p, org.bukkit.scoreboard.Scoreboard sb) {
        Group g = p.isNicked() ? Group.SPIELER : p.getMainGroup();

        Team t = sb.registerNewTeam(g.getScore()+new Random(6).nextString());
        t.setPrefix(g.getPrefix());
        //if (p.isNicked()) t.setSuffix(getNickSuffix(p.getName()));
        t.addEntry(!p.isNicked() ? p.getName() : p.getNickname());
    }

    private String getNickSuffix(String name) {
        String nick = name.length()<=12 ? name : name.substring(0, 9)+"...";
        return "§7 ~" + nick;
    }

}
