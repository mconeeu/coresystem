/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public interface PacketListener {

    void onPacketIn(Player player, Packet<?> packet);

    void onPacketOut(Player player, Packet<?> packet);

}
