/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class NpcInteractEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final NPC npc;
    private final Action action;
    private final CoreWorld world;

    public NpcInteractEvent(Player player, NPC npc, Action action) {
        this.player = player;
        this.npc = npc;
        this.action = action;
        this.world = npc.getWorld();
    }

    public enum Action {
        LEFT_CLICK, RIGHT_CLICK
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
