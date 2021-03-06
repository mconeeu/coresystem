/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import com.google.common.base.Splitter;
import eu.mcone.coresystem.api.bukkit.event.objective.CoreSidebarObjectiveUpdateEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Map;

public abstract class CoreSidebarObjective extends CoreObjective {

    private final static String TEAM_NAME_PREFIX = "o-sdbr-";

    public CoreSidebarObjective(String name) {
        super(DisplaySlot.SIDEBAR, name, "dummy");
    }

    private static String getTeamName(int score) {
        switch (score) {
            case 0:
                return "§0§r";
            case 1:
                return "§1§r";
            case 2:
                return "§2§r";
            case 3:
                return "§3§r";
            case 4:
                return "§4§r";
            case 5:
                return "§5§r";
            case 6:
                return "§6§r";
            case 7:
                return "§7§r";
            case 8:
                return "§8§r";
            case 9:
                return "§9§r";
            case 10:
                return "§a§r";
            case 11:
                return "§b§r";
            case 12:
                return "§c§r";
            case 13:
                return "§d§r";
            case 14:
                return "§e§r";
            case 15:
                return "§f§r";
            default:
                return null;
        }
    }

    @Override
    public void register(CorePlayer player, Scoreboard scoreboard, Objective objective) {
        CoreSidebarObjectiveEntry entry = new CoreSidebarObjectiveEntry();
        onRegister(player, entry);

        CoreSidebarObjectiveUpdateEvent e = new CoreSidebarObjectiveUpdateEvent(this, entry, player.bukkit());
        Bukkit.getPluginManager().callEvent(e);

        if (!e.isCancelled()) {
            if (entry.getTitle() != null) {
                setDisplayName(entry.getTitle());
            }
            if (entry.getScores().size() > 0) {
                for (Map.Entry<Integer, String> score : entry.getScores().entrySet()) {
                    setScore(score.getKey(), score.getValue());
                }
            }

            updateScoreboard();
        }
    }

    @Override
    public void reload(CorePlayer player, Scoreboard scoreboard, Objective objective) {
        CoreSidebarObjectiveEntry entry = new CoreSidebarObjectiveEntry();
        onReload(player, entry);

        CoreSidebarObjectiveUpdateEvent e = new CoreSidebarObjectiveUpdateEvent(this, entry, player.bukkit());
        Bukkit.getPluginManager().callEvent(e);

        if (!e.isCancelled()) {
            if (entry.getTitle() != null) {
                setDisplayName(entry.getTitle());
            }
            if (entry.getScores().size() > 0) {
                for (Map.Entry<Integer, String> score : entry.getScores().entrySet()) {
                    setScore(score.getKey(), score.getValue());
                }
            }

            updateScoreboard();
        }
    }

    /**
     * Set text for a specific Score
     *
     * @param score   the score id
     * @param content text that should be shown (can be up to 32 chars)
     */
    private void setScore(int score, String content) {
        Team team;
        String scoreName = getTeamName(score);

        if (this.scoreboard.getTeam(TEAM_NAME_PREFIX + score) != null) {
            team = this.scoreboard.getTeam(TEAM_NAME_PREFIX + score);
        } else {
            team = this.scoreboard.registerNewTeam(TEAM_NAME_PREFIX + score);
        }

        if (content.length() > 16) {
            List<String> contents = Splitter.fixedLength(16).splitToList(content);
            String lastColors = ChatColor.getLastColors(contents.get(0));

            team.setPrefix(contents.get(0));
            team.setSuffix(lastColors + contents.get(1).substring(0, Math.min(contents.get(1).length(), 10)));
        } else {
            team.setPrefix(content);
            team.setSuffix("");
        }

        this.objective.getScore(scoreName).setScore(score);
        team.addEntry(scoreName);
    }

    /**
     * method called when the scoreboard gets first set to a player
     */
    protected abstract void onRegister(CorePlayer player, CoreSidebarObjectiveEntry objectiveEntry);

    /**
     * method called when the objective or the whole scoreboard gets reloaded
     */
    protected abstract void onReload(CorePlayer player, CoreSidebarObjectiveEntry objectiveEntry);

    /**
     * Updates one or multiple line of a scoreboard
     *
     * @param updateCoreSidebarObjective
     */
    public void update(UpdateCoreSidebarObjective updateCoreSidebarObjective) {
        CoreSidebarObjectiveEntry entry = new CoreSidebarObjectiveEntry();
        updateCoreSidebarObjective.update(entry);

        if (entry.getScores().size() > 0) {
            for (Map.Entry<Integer, String> score : entry.getScores().entrySet()) {
                setScore(score.getKey(), score.getValue());
            }
        }
    }

    @Override
    public void unregister() {
        super.unregister();

        for (Team t : this.scoreboard.getTeams()) {
            if (t.getName().contains(TEAM_NAME_PREFIX))
                t.unregister();
        }
    }

}
