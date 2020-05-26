/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event.npc;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class NpcInteractEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final NPC npc;
    private final PacketPlayInUseEntity.EnumEntityUseAction action;

    public NpcInteractEvent(Player player, NPC npc, PacketPlayInUseEntity.EnumEntityUseAction action) {
        this.player = player;
        this.npc = npc;
        this.action = action;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
