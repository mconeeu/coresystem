/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Class created by qlow | Jan
 */
@AllArgsConstructor
@Getter
public class LabyModMessageSendEvent extends Event implements Cancellable {

    @Getter
    private final static HandlerList handlerList = new HandlerList();

    private Player player;
    private String messageKey, jsonElement;
    @Setter
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
