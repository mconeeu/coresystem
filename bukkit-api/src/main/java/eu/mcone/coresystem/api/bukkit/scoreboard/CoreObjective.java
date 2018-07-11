/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import com.google.common.base.Splitter;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public abstract class CoreObjective {

    private final static String TEAM_NAME_PREFIX = "objective-";

    private final String s;
    private final String s1;
    private org.bukkit.scoreboard.Scoreboard scoreboard;

    @Getter
    private final DisplaySlot slot;
    @Getter
    private org.bukkit.scoreboard.Objective objective;
    @Getter
    private CorePlayer player;

    public CoreObjective(DisplaySlot slot, String s, String s1) {
        this.slot = slot;
        this.s = s;
        this.s1 = s1;
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

    public CoreObjective set(CorePlayer player, Scoreboard scoreboard) {
        this.player = player;
        this.scoreboard = scoreboard;

        objective = scoreboard.registerNewObjective(s, s1);
        objective.setDisplaySlot(slot);

        onRegister(player);
        updateScoreboard();
        return this;
    }

    /**
     * Set text for a specific Score
     * @param score the score id
     * @param content text that should be shown (can be up to 32 chars)
     */
    public void setScore(int score, String content) {
        Team team;
        String scoreName = getTeamName(score);

        if (scoreboard.getTeam(TEAM_NAME_PREFIX+score) != null) {
            team = scoreboard.getTeam(TEAM_NAME_PREFIX+score);
        } else {
            team = scoreboard.registerNewTeam(TEAM_NAME_PREFIX+score);
        }

        if (content.length() > 16) {
            List<String> contents = Splitter.fixedLength(16).splitToList(content);
            String lastColors = ChatColor.getLastColors(contents.get(0));

            team.setPrefix(contents.get(0));
            team.setSuffix(lastColors + contents.get(1).substring(0, Math.min(contents.get(1).length(), 10)));
        } else {
            team.setPrefix(content);
        }

        objective.getScore(scoreName).setScore(score);
        team.addEntry(scoreName);
    }

    /**
     * change the displayname (headline) of the Objective
     * mind to updateScorebord() after this. Otherwise it will have no effect
     * @param name name
     * @return this
     */
    public CoreObjective setDisplayName(String name) {
        objective.setDisplayName(name);
        return this;
    }

    /**
     * update the changes you've made and set the scoreboard to the player
     * @return this
     */
    public CoreObjective updateScoreboard() {
        player.bukkit().setScoreboard(scoreboard);
        return this;
    }

    /**
     * call this method if you want to execute the written code in the onReload() method
     * the scoreboard gets set to the player after this
     */
    public void reload() {
        onReload(player);
        updateScoreboard();
    }

    /**
     * method called when the scoreboard gets first set to a player
     */
    protected abstract void onRegister(CorePlayer player);

    /**
     * method called when the objective or the whole scoreboard gets reloaded
     */
    protected abstract void onReload(CorePlayer player);

    void unregister() {
        for (Team t : scoreboard.getTeams()) {
            if (t.getName().contains(TEAM_NAME_PREFIX)) t.unregister();
        }
    }

    /**
     * get the bukkit Scoreboard object
     * @return bukkit scoreboard
     */
    public org.bukkit.scoreboard.Objective bukkit() {
        return objective;
    }

}
