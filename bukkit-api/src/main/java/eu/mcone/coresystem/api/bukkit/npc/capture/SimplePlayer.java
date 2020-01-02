package eu.mcone.coresystem.api.bukkit.npc.capture;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SimplePlayer {
    @Getter
    protected boolean playing = true;

    protected AtomicInteger currentTick;
    protected boolean forward = true;
    protected boolean backward = false;

    protected BukkitTask playingTask;


    public abstract void play();

    public void restart() {
        if (playing) {
            currentTick = new AtomicInteger(0);
        } else {
            playing = true;
            play();
        }
    }

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
