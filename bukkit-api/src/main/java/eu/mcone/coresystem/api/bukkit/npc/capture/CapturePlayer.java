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

public abstract class CapturePlayer {

    @Getter
    protected boolean playing = true;
    protected boolean forward = true;
    protected AtomicInteger tick;

    protected HashSet<org.bukkit.entity.Player> viewer;

    protected BukkitTask task;

    public CapturePlayer() {
        tick = new AtomicInteger();
        viewer = new HashSet<>();
    }

    public abstract void play();

    public void restart() {
        if (playing) {
            tick = new AtomicInteger(0);
        } else {
            playing = true;
            play();
        }
    }

    public void playing(boolean playing) {
        this.playing = playing;
    }

    public void forward(boolean forward) {
        this.forward = forward;
    }

    public void stop() {
        playing = false;
        task.cancel();
    }

    public void addViewer(final org.bukkit.entity.Player player) {
        viewer.add(player);
    }

    public void addViewer(final org.bukkit.entity.Player... players) {
        viewer.addAll(Arrays.asList(players));
    }

    public void removeViewer(final org.bukkit.entity.Player player) {
        viewer.remove(player);
    }

    public void removeViewer(final org.bukkit.entity.Player... players) {
        viewer.removeAll(Arrays.asList(players));
    }

    public Collection<org.bukkit.entity.Player> getViewer() {
        return new ArrayList<>(viewer);
    }

    public int getTick() {
        return tick.get();
    }
}
