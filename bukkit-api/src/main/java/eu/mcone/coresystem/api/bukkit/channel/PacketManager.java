/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.util.PacketListener;

public interface PacketManager {

    void registerPacketListener(PacketListener... packetListeners);

    void unregisterPacketListener(PacketListener listener);

}
