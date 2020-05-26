/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SimpleRecorder implements Listener, Serializable {

    private static final long serialVersionUID = 191955L;

    @Getter
    protected int ticks;

    @Getter
    protected transient boolean isStopped = false;

    protected transient BukkitTask taskID;
    protected AtomicInteger savedPackets;

    public abstract void record();

}
