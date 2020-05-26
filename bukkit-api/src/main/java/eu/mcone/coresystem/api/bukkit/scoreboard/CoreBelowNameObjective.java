/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class CoreBelowNameObjective extends CoreObjective {

    private final String displayname;

    public CoreBelowNameObjective(String name, String displayname) {
        super(DisplaySlot.BELOW_NAME, name, "dummy");
        this.displayname = displayname;
    }

    @Override
    public void register(CorePlayer player, Scoreboard scoreboard, Objective objective) {
        setDisplayName(displayname);

        for (CorePlayer p : CoreSystem.getInstance().getOnlineCorePlayers()) {
            objective.getScore(p.getName()).setScore(getPlayerScore(player, p));
        }
        updateScoreboard();
    }

    @Override
    public void reload(CorePlayer player, Scoreboard scoreboard, Objective objective) {
        for (CorePlayer p : CoreSystem.getInstance().getOnlineCorePlayers()) {
            objective.getScore(p.getName()).setScore(getPlayerScore(player, p));
        }
        updateScoreboard();
    }

    protected abstract int getPlayerScore(CorePlayer owner, CorePlayer target);

}
