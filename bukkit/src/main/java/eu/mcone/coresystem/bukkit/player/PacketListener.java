/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.event.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.util.ReflectionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketListener extends MessageToMessageDecoder<Packet<?>> {

    private final Player player;
    private final ChannelPipeline pipeline;

    PacketListener(Player player) {
        this.player = player;
        this.pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addAfter("decoder", "PacketInjector", this);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> object, List<Object> list) {
        list.add(object);

        if (object instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) object;

            if (packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) || packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)) {
                NPC npc = BukkitCoreSystem.getSystem().getNpcManager().getNPC((int) ReflectionManager.getValue(packet, "a"));

                if (npc != null) {
                    Bukkit.getScheduler().runTask(
                            BukkitCoreSystem.getSystem(),
                            () -> Bukkit.getPluginManager().callEvent(new NpcInteractEvent(player, npc, packet.a()))
                    );
                }
            }
        }
    }

    public void remove() {
        if (pipeline.get(getClass()) != null) {
            pipeline.remove(this);
        }
    }

}
