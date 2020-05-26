/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture;

public interface MotionPlayer {

    MotionCaptureData getData();

    boolean isPlaying();

    void play();

    void restart();

    void stopPlaying();

    void startPlaying();

    void backward();

    void forward();

    void stop();

    int getCurrentTick();
}
