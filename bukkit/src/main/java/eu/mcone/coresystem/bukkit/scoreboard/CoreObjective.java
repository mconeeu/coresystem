/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.scoreboard;

import eu.mcone.coresystem.bukkit.player.CorePlayer;
import lombok.Getter;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public abstract class CoreObjective {

    protected final String s;
    protected final String s1;
    @Getter
    protected final DisplaySlot slot;
    @Getter
    protected org.bukkit.scoreboard.Scoreboard scoreboard;
    @Getter
    protected org.bukkit.scoreboard.Objective objective;
    @Getter
    protected CorePlayer player;

    public CoreObjective(DisplaySlot slot, String s, String s1) {
        this.slot = slot;
        this.s = s;
        this.s1 = s1;
    }

    public CoreObjective set(CorePlayer player, Scoreboard scoreboard) {
        this.player = player;
        this.scoreboard = scoreboard;

        objective = scoreboard.registerNewObjective(s, s1);
        objective.setDisplaySlot(slot);
        register();

        return this;
    }

    public abstract void register();

    public abstract void reload();

    public org.bukkit.scoreboard.Objective bukkit() {
        return objective;
    }

}
