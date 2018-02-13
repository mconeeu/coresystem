/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.event;

import eu.mcone.coresystem.bukkit.player.CorePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoinsChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final CorePlayer player;

    public CoinsChangeEvent(CorePlayer p) {
        this.player = p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CorePlayer getPlayer() {
        return player;
    }
}
