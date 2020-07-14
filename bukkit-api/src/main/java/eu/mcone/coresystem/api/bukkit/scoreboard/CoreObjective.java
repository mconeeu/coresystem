/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class CoreObjective {

    @Getter
    protected final String name;
    protected final String criteria;
    protected org.bukkit.scoreboard.Scoreboard scoreboard;
    protected org.bukkit.scoreboard.Objective objective;

    @Getter
    protected final DisplaySlot slot;
    @Getter
    protected CorePlayer player;

    public CoreObjective(DisplaySlot slot, String name, String criteria) {
        this.slot = slot;
        this.name = name;
        this.criteria = criteria;
    }

    public CoreObjective set(CorePlayer player, Scoreboard scoreboard) {
        this.player = player;
        this.scoreboard = scoreboard;

        objective = scoreboard.registerNewObjective(name, criteria);
        objective.setDisplaySlot(slot);
        register(player, scoreboard, objective);

        return this;
    }

    /**
     * change the displayname (headline) of the Objective
     * mind to updateScorebord() after this. Otherwise it will have no effect
     *
     * @param name name
     * @return this
     */
    public CoreObjective setDisplayName(String name) {
        objective.setDisplayName(name);
        return this;
    }

    /**
     * update the changes you've made and set the scoreboard to the player
     *
     * @return this
     */
    public CoreObjective updateScoreboard() {
        player.bukkit().setScoreboard(scoreboard);
        return this;
    }

    public void reload() {
        reload(player, scoreboard, objective);
    }

    /**
     * method called when objective is registered first time
     */
    public abstract void register(CorePlayer player, Scoreboard scoreboard, Objective objective);

    /**
     * method to reload/update important things
     */
    public abstract void reload(CorePlayer player, Scoreboard scoreboard, Objective objective);

    /**
     * method to unregister all created Teams
     */
    public void unregister() {
        objective.unregister();
    }

    /**
     * get the bukkit Scoreboard object
     *
     * @return bukkit scoreboard
     */
    public org.bukkit.scoreboard.Objective bukkit() {
        return objective;
    }


}
