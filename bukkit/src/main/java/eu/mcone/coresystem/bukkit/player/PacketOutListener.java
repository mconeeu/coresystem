package eu.mcone.coresystem.bukkit.player;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketOutListener extends PlayerConnection {

    PacketOutListener(final EntityPlayer player) {
        super(player.server, player.playerConnection.networkManager, player);
        this.player = player;
    }

    PacketOutListener(Player player) {
        this(getNMSPlayer(player));
        this.player = getNMSPlayer(player);
    }

    //Packets to send
    @Override
    public void sendPacket(Packet packet) {
        //Send the packet
        super.sendPacket(packet);
    }

    public static EntityPlayer getNMSPlayer(Player p) {
        return ((CraftPlayer)p).getHandle();
    }
}
