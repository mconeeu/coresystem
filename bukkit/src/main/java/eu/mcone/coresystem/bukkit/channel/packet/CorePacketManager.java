/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel.packet;

import eu.mcone.coresystem.api.bukkit.channel.PacketManager;
import eu.mcone.coresystem.api.bukkit.util.PacketListener;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorePacketManager implements PacketManager {

    private final List<PacketListener> listeners = new ArrayList<>();

    @Override
    public void registerPacketListener(PacketListener... packetListeners) {
        this.listeners.addAll(Arrays.asList(packetListeners));
    }

    @Override
    public void unregisterPacketListener(PacketListener listener) {
        this.listeners.remove(listener);
    }

    public void disable() {
        this.listeners.clear();
    }

    void onPacketIn(Player p, Packet<?> packet) {
        for (PacketListener listener : listeners) {
            listener.onPacketIn(p, packet);
        }
    }

    void onPacketOut(Player p, Packet<?> packet) {
        for (PacketListener listener : listeners) {
            listener.onPacketOut(p, packet);
        }
    }

}
