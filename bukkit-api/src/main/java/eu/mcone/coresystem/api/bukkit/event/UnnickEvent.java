/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class UnnickEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CorePlayer player;
    private final boolean skinBypassed;
    @Setter
    private boolean cancelled;

    public UnnickEvent(CorePlayer p, boolean skinBypassed) {
        this.player = p;
        this.skinBypassed = skinBypassed;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
