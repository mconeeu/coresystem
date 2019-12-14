/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class CorePlayerLoadedEvent extends Event {

    public enum Reason {
        JOIN,
        RELOAD
    }

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Reason loadReason;
    private final CorePlayer player;
    private final Player bukkitPlayer;
    @Setter
    private boolean hidden;

    public HandlerList getHandlers() {
        return handlerList;
    }

}
