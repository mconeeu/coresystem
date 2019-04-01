/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class StatsChangeEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Gamemode gamemode;
    private final CorePlayer player;

    public StatsChangeEvent(CorePlayer player, Gamemode gamemode) {
        this.player = player;
        this.gamemode = gamemode;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}