/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event.player;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class PlayerVanishEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CorePlayer player;
    private final boolean isVanished;
    private String cancelCause;
    @Setter
    private boolean cancelled;

    public void setCancelled(boolean cancelled, String cancelCause) {
        this.cancelled = cancelled;
        this.cancelCause = cancelCause;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}