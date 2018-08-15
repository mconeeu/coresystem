/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Class created by qlow | Jan
 */
@AllArgsConstructor
@Getter
public class LabyModPlayerJoinEvent extends Event {

    @Getter
    private final static HandlerList handlerList = new HandlerList();

    private Player player;
    private LabyModConnection connection;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
