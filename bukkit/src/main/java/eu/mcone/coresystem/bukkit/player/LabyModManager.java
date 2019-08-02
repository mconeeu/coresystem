/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.channel.LMCListener;
import eu.mcone.coresystem.bukkit.channel.LabyModMessageListener;
import eu.mcone.coresystem.core.labymod.LMCUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.PacketDataSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LabyModManager extends LMCUtils<Player> {

    public LabyModManager() {
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "mcone:labymod", new LabyModMessageListener(this));
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "mcone:lmc", new LMCListener(this));
    }

    public void disable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "mcone:labymod");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "mcone:lmc");
    }

    @Override
    protected void sendLMCMessage(Player player, byte[] message) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutCustomPayload(new MinecraftKey("lmc"), new PacketDataSerializer(Unpooled.wrappedBuffer(message)))
        );
    }

}
