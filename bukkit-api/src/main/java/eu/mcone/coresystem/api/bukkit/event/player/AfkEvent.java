/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event.player;

import eu.mcone.coresystem.api.core.player.PlayerState;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class AfkEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final PlayerState state;

    public AfkEvent(Player player, PlayerState state) {
        this.player = player;
        this.state = state;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
