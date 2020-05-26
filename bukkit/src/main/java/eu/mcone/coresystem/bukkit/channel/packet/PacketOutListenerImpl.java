/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel.packet;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketOutListenerImpl extends PlayerConnection {

    private Player player;

    public PacketOutListenerImpl(Player player) {
        this(((CraftPlayer) player).getHandle());
        this.player = player;
    }

    private PacketOutListenerImpl(EntityPlayer player) {
        super(player.server, player.playerConnection.networkManager, player);
    }

    @Override
    public void sendPacket(Packet packet) {
        BukkitCoreSystem.getSystem().getPacketManager().onPacketIn(player, packet);
        super.sendPacket(packet);
    }

}
