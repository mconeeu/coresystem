package eu.mcone.coresystem.api.bukkit.npc.capture;

import org.bukkit.entity.Player;

public interface MotionRecorder {

    Player getPlayer();

    String getRecorderID();

    String getName();

    String getWorld();

    long getRecorded();

    int getTicks();

    MotionChunk getChunk();

    boolean isStopped();

    void stop();
}
