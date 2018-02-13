/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.scoreboard;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public abstract class Scoreboard {

    protected org.bukkit.scoreboard.Scoreboard scoreboard;
    protected Map<DisplaySlot, Objective> objectives;
    protected CorePlayer player;

    public Scoreboard(CorePlayer p) {
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = sm.getNewScoreboard();

        this.player = p;
        this.scoreboard = scoreboard;
        this.objectives = new HashMap<>();

        for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
            setPlayerTeams(player, scoreboard);
        }
        p.bukkit().setScoreboard(scoreboard);
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
        objectives.put(objective.getSlot(), objective);
    }

    public org.bukkit.scoreboard.Scoreboard bukkit() {
        return scoreboard;
    }

    public Objective getObjective(DisplaySlot slot) {
        return objectives.getOrDefault(slot, null);
    }

    public CorePlayer getPlayer() {
        return player;
    }
}
