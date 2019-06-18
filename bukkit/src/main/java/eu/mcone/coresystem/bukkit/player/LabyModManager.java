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
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LabyModManager extends LMCUtils<Player> {

    public LabyModManager() {
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LABYMOD", new LabyModMessageListener(this));
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LMC", new LMCListener(this));
    }

    public void disable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LABYMOD");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LMC");
    }

    @Override
    protected void sendLMCMessage(Player player, byte[] message) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutCustomPayload("LMC", new PacketDataSerializer(Unpooled.wrappedBuffer(message)))
        );
    }

}
