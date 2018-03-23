/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.scoreboard;

import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import eu.mcone.coresystem.lib.util.RandomString;
import org.bukkit.scoreboard.Team;

public class MainScoreboard extends CoreScoreboard {

    @Override
    public void setPlayerTeams(CorePlayer p, org.bukkit.scoreboard.Scoreboard sb) {
        Group g = p.isNicked() ? Group.SPIELER : p.getMainGroup();

        Team t = sb.registerNewTeam(g.getScore()+new RandomString(6).nextString());
        t.setPrefix(g.getPrefix());
        //if (p.isNicked()) t.setSuffix(getNickSuffix(p.getName()));
        t.addEntry(!p.isNicked() ? p.getName() : p.getNickname());
    }

    private String getNickSuffix(String name) {
        String nick = name.length()<=12 ? name : name.substring(0, 9)+"...";
        return "§7 ~" + nick;
    }

}
