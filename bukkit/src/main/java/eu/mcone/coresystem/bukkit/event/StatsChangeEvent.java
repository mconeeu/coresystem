/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.event;

import eu.mcone.coresystem.bukkit.api.StatsAPI;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatsChangeEvent extends Event {

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