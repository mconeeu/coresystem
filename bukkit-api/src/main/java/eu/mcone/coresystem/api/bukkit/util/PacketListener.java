package eu.mcone.coresystem.api.bukkit.util;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public interface PacketListener {

    void onPacketIn(Player player, Packet<?> packet);

    void onPacketOut(Player player, Packet<?> packet);

}
