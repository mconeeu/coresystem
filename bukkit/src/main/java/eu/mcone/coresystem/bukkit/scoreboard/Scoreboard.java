/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.scoreboard;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public abstract class Scoreboard {

    @Getter
    protected org.bukkit.scoreboard.Scoreboard scoreboard;
    protected Map<DisplaySlot, Objective> objectives;
    @Getter
    protected CorePlayer player;

    public Scoreboard() {
        ScoreboardManager sm = Bukkit.getScoreboardManager();

        this.scoreboard = sm.getNewScoreboard();
        this.objectives = new HashMap<>();
    }

    public Scoreboard set(CorePlayer p) {
        this.player = p;

        for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
            setPlayerTeams(player, scoreboard);
        }
        p.bukkit().setScoreboard(scoreboard);

        return this;
    }

    public abstract void setPlayerTeams(CorePlayer p, org.bukkit.scoreboard.Scoreboard sb);

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerTeams(CoreSystem.getCorePlayer(player), scoreboard);
        }
        player.bukkit().setScoreboard(bukkit());

        for (Objective o : objectives.values()) o.reload();
    }

    public void setNewObjective(Objective objective) {
        objectives.put(objective.getSlot(), objective.set(player, scoreboard));
    }

    public org.bukkit.scoreboard.Scoreboard bukkit() {
        return scoreboard;
    }

    public Objective getObjective(DisplaySlot slot) {
        return objectives.getOrDefault(slot, null);
    }

}
