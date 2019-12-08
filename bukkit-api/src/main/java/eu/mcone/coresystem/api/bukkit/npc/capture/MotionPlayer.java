package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class MotionPlayer {
    @Getter
    public MotionCaptureData data;
    @Getter
    protected boolean playing = true;

    protected PlayerNpc playerNpc;
    protected AtomicInteger currentTick;
    protected boolean forward = true;
    protected boolean backward = false;

    protected BukkitTask playingTask;

    public MotionPlayer(final PlayerNpc playerNpc, final MotionCaptureData data) {
        this.playerNpc = playerNpc;
        this.data = data;
    }

    public abstract void playMotionCapture();

    public void stopPlaying() {
        playing = false;
    }

    public void startPlaying() {
        playing = true;
    }

    public void backward() {
        forward = false;
        backward = true;
    }

    public void forward() {
        forward = true;
        backward = false;
    }

    public void stop() {
        playing = false;
        playingTask.cancel();
    }

    public int getCurrentTick() {
        return currentTick.get();
    }
}
