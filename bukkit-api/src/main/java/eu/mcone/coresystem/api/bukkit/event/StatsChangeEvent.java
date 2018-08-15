/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.Stats;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class StatsChangeEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Stats stats;
    private final CorePlayer player;

    public StatsChangeEvent(CorePlayer player, Stats stats) {
        this.player = player;
        this.stats = stats;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}