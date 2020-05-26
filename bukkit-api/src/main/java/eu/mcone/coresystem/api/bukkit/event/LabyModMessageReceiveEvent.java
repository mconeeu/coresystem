/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class LabyModMessageReceiveEvent extends Event {

    @Getter
    private final static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final String messageKey;
    private final JsonElement jsonElement;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
