/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import org.bukkit.scoreboard.Team;

public final class MainScoreboard extends CoreScoreboard {

    @Override
    public Team modifyTeam(CorePlayer owner, CorePlayer p, Team t) {
        Group g = p.isNicked() ? Group.SPIELER : p.getMainGroup();
        t.setPrefix(g.getPrefix());

        return t;
    }

    private String getNickSuffix(String name) {
        String nick = name.length()<=12 ? name : name.substring(0, 9)+"...";
        return "§7 ~" + nick;
    }

}
