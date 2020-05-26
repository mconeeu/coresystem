/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class HologramManagerReloadedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final HologramManager holoManager;

    public HandlerList getHandlers() {
        return handlerList;
    }
    
}
