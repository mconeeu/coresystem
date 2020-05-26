/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MotionRecorder {

    String getRecorderName();

    String getWorld();

    String getName();

    long getRecorded();

    void record();

    int getTicks();

    boolean isStopped();

    HashMap<String, List<PacketContainer>> getPackets();

    Map<String, List<PacketContainer>> stopRecording();

}
