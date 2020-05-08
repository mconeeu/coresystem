/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreScoreboard {

    private final Map<DisplaySlot, CoreObjective> objectives;

    @Getter
    private final org.bukkit.scoreboard.Scoreboard scoreboard;
    @Getter
    private CorePlayer player;

    public CoreScoreboard() {
        ScoreboardManager sm = Bukkit.getScoreboardManager();

        this.scoreboard = sm.getNewScoreboard();
        this.objectives = new HashMap<>();
    }

    public CoreScoreboard set(CorePlayer player) {
        this.player = player;
        reload();

        return this;
    }

    public abstract void modifyTeam(CorePlayer owner, CorePlayer player, CoreScoreboardEntry entry);

    /**
     * reload the set scoreboard values
     */
    public void reload() {
        for (CorePlayer p : CoreSystem.getInstance().getOnlineCorePlayers()) {
            CoreScoreboardEntry scoreboardEntry = new CoreScoreboardEntry(p);
            modifyTeam(this.player, p, scoreboardEntry);
            scoreboardEntry.setTeam(scoreboard);
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
