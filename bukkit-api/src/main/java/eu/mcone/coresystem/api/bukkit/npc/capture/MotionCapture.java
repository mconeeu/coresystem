package eu.mcone.coresystem.api.bukkit.npc.capture;

public interface MotionCapture {

    String getName();

    String getCreator();

    long getRecorded();

    String getWorld();

    int getLength();

    MotionChunk getMotionChunk();
}
