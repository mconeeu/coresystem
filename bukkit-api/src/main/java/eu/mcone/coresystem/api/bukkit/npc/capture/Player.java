/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player {
    @Getter
    protected boolean playing = true;

    protected AtomicInteger currentTick;
    protected boolean forward = true;
    protected boolean backward = false;

    protected BukkitTask playingTask;

    protected HashSet<org.bukkit.entity.Player> watcher = new HashSet<>();

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

    public void addWatcher(final org.bukkit.entity.Player player) {
        watcher.add(player);
    }

    public void addWatcher(final org.bukkit.entity.Player... players) {
        watcher.addAll(Arrays.asList(players));
    }

    public void removeWatcher(final org.bukkit.entity.Player player) {
        watcher.remove(player);
    }

    public void removeWatcher(final org.bukkit.entity.Player... players) {
        watcher.removeAll(Arrays.asList(players));
    }

    public Collection<org.bukkit.entity.Player> getWatchers() {
        return new ArrayList<>(watcher);
    }

    public int getCurrentTick() {
        return currentTick.get();
    }
}
