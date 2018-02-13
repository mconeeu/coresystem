/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.scoreboard;

import eu.mcone.coresystem.bukkit.player.CorePlayer;
import org.bukkit.scoreboard.DisplaySlot;

public abstract class Objective {

    protected org.bukkit.scoreboard.Scoreboard scoreboard;
    protected org.bukkit.scoreboard.Objective objective;
    private DisplaySlot slot;
    protected CorePlayer player;

    public Objective(CorePlayer p, DisplaySlot slot, String s, String s1) {
        this.player = p;
        this.slot = slot;

        scoreboard = p.getScoreboard().bukkit();
        objective = scoreboard.registerNewObjective(s, s1);
        objective.setDisplaySlot(slot);
        register();
    }

    public abstract void register();

    public abstract void reload();

    public org.bukkit.scoreboard.Scoreboard getScoreboard() {
        return scoreboard;
    }

    public org.bukkit.scoreboard.Objective bukkit() {
        return objective;
    }

    public DisplaySlot getSlot() {
        return slot;
    }

    public CorePlayer getPlayer() {
        return player;
    }


}
