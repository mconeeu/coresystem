/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreScoreboard {

    @Getter
    protected org.bukkit.scoreboard.Scoreboard scoreboard;
    protected Map<DisplaySlot, CoreObjective> objectives;
    @Getter
    protected BukkitCorePlayer player;

    public CoreScoreboard() {
        ScoreboardManager sm = Bukkit.getScoreboardManager();

        this.scoreboard = sm.getNewScoreboard();
        this.objectives = new HashMap<>();
    }

    public CoreScoreboard set(CoreSystem instance, BukkitCorePlayer p) {
        this.player = p;

        for (BukkitCorePlayer player : instance.getOnlineCorePlayers()) {
            setPlayerTeams(player, scoreboard);
        }
        p.bukkit().setScoreboard(scoreboard);

        return this;
    }

    public abstract void setPlayerTeams(BukkitCorePlayer p, org.bukkit.scoreboard.Scoreboard sb);

    public void reload(CoreSystem instance) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerTeams(instance.getCorePlayer(player), scoreboard);
        }
        player.bukkit().setScoreboard(bukkit());

        for (CoreObjective o : objectives.values()) o.reload();
    }

    public void setNewObjective(CoreObjective objective) {
        objectives.put(objective.getSlot(), objective.set(player, scoreboard));
    }

    public org.bukkit.scoreboard.Scoreboard bukkit() {
        return scoreboard;
    }

    public CoreObjective getObjective(DisplaySlot slot) {
        return objectives.getOrDefault(slot, null);
    }

}