/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class StatsChangeEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Gamemode gamemode;
    private final CorePlayer player;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}