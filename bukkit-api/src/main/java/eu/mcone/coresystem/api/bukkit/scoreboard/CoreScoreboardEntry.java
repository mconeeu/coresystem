/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import group.onegaming.networkmanager.core.api.random.UniqueIdType;
import lombok.Getter;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
public class CoreScoreboardEntry {

    private final CorePlayer player;
    private int priority;
    private String name, prefix, suffix;

    CoreScoreboardEntry(CorePlayer player) {
        this.player = player;
    }

    public CoreScoreboardEntry priority(int priority) {
        this.priority = priority;
        return this;
    }

    public CoreScoreboardEntry name(String name) {
        this.name = name;
        return this;
    }

    public CoreScoreboardEntry prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CoreScoreboardEntry suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    Team setTeam(Scoreboard scoreboard) {
        Team team = scoreboard.registerNewTeam(priority + "_" + CoreSystem.getInstance().getUniqueIdUtil().getTmpUniqueKey(UniqueIdType.STRING, false));
        if (name != null) {
            team.setDisplayName(name);
        }
        if (prefix != null) {
            team.setPrefix(prefix);
        }
        if (suffix != null) {
            team.setSuffix(suffix);
        }
        team.addEntry(player.isNicked() ? player.getNick().getName() : player.getName());

        return team;
    }

}
