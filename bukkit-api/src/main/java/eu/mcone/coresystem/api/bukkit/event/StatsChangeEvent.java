/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.StatsAPI;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class StatsChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final StatsAPI stats;
    @Getter
    private final CorePlayer player;

    public StatsChangeEvent(CorePlayer player, StatsAPI stats) {
        this.player = player;
        this.stats = stats;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}