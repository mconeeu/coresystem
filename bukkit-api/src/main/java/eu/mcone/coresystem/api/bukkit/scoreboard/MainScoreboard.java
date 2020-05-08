/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;

public final class MainScoreboard extends CoreScoreboard {

    @Override
    public void modifyTeam(CorePlayer owner, CorePlayer p, CoreScoreboardEntry t) {
        Group g = p.isNicked() ? p.getNick().getGroup() : p.getMainGroup();
        t.prefix(g.getPrefix());

        if (p.isVanished()) {
            t.suffix(" §3§lⓋ");
        }
    }

}
