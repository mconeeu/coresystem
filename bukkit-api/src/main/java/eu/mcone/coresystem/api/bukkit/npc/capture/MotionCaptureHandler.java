/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;

import java.util.List;

public interface MotionCaptureHandler {

    MotionCaptureScheduler getMotionCaptureScheduler();

    CodecRegistry getCodecRegistry();

    void loadDatabase();

    boolean saveMotionCapture(MotionRecorder recorder);

    MotionCapture getMotionCapture(final String name);

    void deleteMotionCapture(final String name);

    boolean existsMotionCapture(final String name);

    List<MotionCapture> getMotionCaptures();

    interface MotionCaptureScheduler {
        void addNPCs(final PlayerNpc... playerNpcs);

        void addNpc(final PlayerNpc playerNpc);

        boolean removeNpc(final PlayerNpc npc);

        boolean removeNpc(final String name);

        List<PlayerNpc> getNpcs();
    }
}
