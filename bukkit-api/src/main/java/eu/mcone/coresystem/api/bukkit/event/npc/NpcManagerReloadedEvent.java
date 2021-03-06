/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event.npc;

import eu.mcone.coresystem.api.bukkit.npc.NpcManager;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class NpcManagerReloadedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final NpcManager npcManager;

    public NpcManagerReloadedEvent(NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
