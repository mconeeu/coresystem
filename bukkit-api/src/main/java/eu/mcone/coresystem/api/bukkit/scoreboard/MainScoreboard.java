/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;

import java.util.UUID;

public final class MainScoreboard extends CoreScoreboard {

    @Override
    public void modifyTeam(CorePlayer owner, CorePlayer p, CoreScoreboardEntry t) {
        Group g = p.isNicked() ? p.getNick().getGroup() : p.getMainGroup();
        t.priority(p.getUuid().equals(UUID.fromString("44b8a5d6-c2c3-4576-997f-71b94f5eb7e0")) ? 0 : g.getScore()).prefix(g.getPrefix());

        if (p.isVanished()) {
            t.suffix(" §3§lⓋ");
        }
    }
}
