package eu.mcone.coresystem.api.bukkit.npc.capture;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class SimplePlayer {
    @Getter
    protected boolean playing = true;

    protected AtomicInteger currentTick;
    protected boolean forward = true;
    protected boolean backward = false;

    protected BukkitTask playingTask;

    @Getter@Setter
    protected double speed = 1;

    protected HashSet<Player> watcher = new HashSet<>();

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

    public void addWatcher(final Player player) {
        watcher.add(player);
    }

    public void addWatcher(final Player... players) {
        watcher.addAll(Arrays.asList(players));
    }

    public void removeWatcher(final Player player) {
        watcher.remove(player);
    }

    public void removeWatcher(final Player... players) {
        watcher.removeAll(Arrays.asList(players));
    }

    public Collection<Player> getWatchers() {
        return new ArrayList<>(watcher);
    }

    public int getCurrentTick() {
        return currentTick.get();
    }
}
