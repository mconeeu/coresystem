/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel.packet;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.NoSuchElementException;

public class PacketInListenerImpl extends MessageToMessageDecoder<Packet<?>> {

    private final Player player;
    private final ChannelPipeline pipeline;

    public PacketInListenerImpl(Player player) {
        this.player = player;
        this.pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        try {
            pipeline.addAfter("decoder", "PacketInjector", this);
        } catch (NoSuchElementException e) {
            BukkitCoreSystem.getSystem().sendConsoleMessage("§cCould not set PacketListener for "+player.getName()+". Missing decoder!");
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) {
        BukkitCoreSystem.getSystem().getPacketManager().onPacketIn(player, packet);
        list.add(packet);
    }

    public void remove() {
        pipeline.flush();

        if (pipeline.get(getClass()) != null) {
            pipeline.remove(this);
        }
    }
}
