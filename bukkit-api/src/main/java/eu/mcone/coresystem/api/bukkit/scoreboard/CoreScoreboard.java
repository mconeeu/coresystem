/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.util.Random;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreScoreboard {

    private Map<DisplaySlot, CoreObjective> objectives;

    @Getter
    private org.bukkit.scoreboard.Scoreboard scoreboard;
    @Getter
    private CorePlayer player;

    public CoreScoreboard() {
        ScoreboardManager sm = Bukkit.getScoreboardManager();

        this.scoreboard = sm.getNewScoreboard();
        this.objectives = new HashMap<>();
    }

    public CoreScoreboard set(CoreSystem instance, CorePlayer player) {
        this.player = player;
        reload(instance);

        return this;
    }

    public abstract Team modifyTeam(CorePlayer owner, CorePlayer player, Team team);

    /**
     * reload the set scoreboard values
     * @param instance CoreSystem instance
     */
    public void reload(CoreSystem instance) {
        for (CorePlayer p : instance.getOnlineCorePlayers()) {
            Team team = scoreboard.registerNewTeam(p.getMainGroup().getScore()+new Random(6).nextString());
            team = modifyTeam(this.player, p, team);
            team.addEntry(p.isNicked() ? p.getNickname() : p.getName());
        }
        player.bukkit().setScoreboard(scoreboard);

        for (CoreObjective o : objectives.values()) o.reload();
    }

    public void setNewObjective(CoreObjective objective) {
        if (objectives.get(objective.getSlot()) != null) objectives.get(objective.getSlot()).unregister();
        objectives.put(objective.getSlot(), objective.set(player, scoreboard));
    }

    public void unregister() {
        for (Team t : scoreboard.getTeams()) t.unregister();
    }

    public org.bukkit.scoreboard.Scoreboard bukkit() {
        return scoreboard;
    }

    public CoreObjective getObjective(DisplaySlot slot) {
        return objectives.getOrDefault(slot, null);
    }

}
