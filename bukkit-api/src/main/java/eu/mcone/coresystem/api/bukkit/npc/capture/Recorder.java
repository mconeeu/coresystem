/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public abstract class Recorder {

    protected final String recorderID;
    protected final String world;
    protected long recorded;
    protected int ticks;
    protected boolean stopped;

    protected AtomicInteger savedPackets;

    public Recorder(String recorderID, String world) {
        this.recorderID = recorderID;
        this.world = world;
        recorded = System.currentTimeMillis() / 1000;
        ticks = 0;
        savedPackets = new AtomicInteger();
    }

    public abstract void record();

    public abstract void stop();
}
