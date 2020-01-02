package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketWrapper;

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

    HashMap<String, List<PacketWrapper>> getPackets();

    Map<String, List<PacketWrapper>> stopRecording();

}
