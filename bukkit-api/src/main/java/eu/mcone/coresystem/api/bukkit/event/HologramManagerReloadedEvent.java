/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class HologramManagerReloadedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final HologramManager holoManager;

    public HologramManagerReloadedEvent(HologramManager holoManager) {
        this.holoManager = holoManager;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }
    
}
