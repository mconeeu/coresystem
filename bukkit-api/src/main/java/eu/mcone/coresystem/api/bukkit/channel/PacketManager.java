package eu.mcone.coresystem.api.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.util.PacketListener;

public interface PacketManager {

    void registerPacketListener(PacketListener... packetListeners);

    void unregisterPacketListener(PacketListener listener);

}
