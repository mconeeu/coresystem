/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.event.nms.PacketReceiveEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketInListener extends MessageToMessageDecoder<Packet<?>> {

    private final Player player;
    private final ChannelPipeline pipeline;

    PacketInListener(Player player) {
        this.player = player;
        this.pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addAfter("decoder", "PacketInjector", this);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> object, List<Object> list) {
        list.add(object);
        Bukkit.getPluginManager().callEvent(new PacketReceiveEvent(object, list, player));
    }

    public void remove() {
        if (pipeline.get(getClass()) != null) {
            pipeline.remove(this);
        }
    }
}
