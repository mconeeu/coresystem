package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;

import java.util.List;

public interface MotionCaptureHandler {

    MotionCaptureScheduler getMotionCaptureScheduler();

    void loadDatabase();

    boolean saveMotionCapture(final MotionRecorder recorder);

    MotionCaptureData getMotionCapture(final String name);

    void deleteMotionCapture(final MotionCaptureData data);

    void deleteMotionCapture(final String name);

    boolean existsMotionCapture(final String name);

    List<MotionCaptureData> getMotionCaptures();

    interface MotionCaptureScheduler {
        void addNpcs(final PlayerNpc... playerNpcs);

        void addNpc(final PlayerNpc playerNpc);

        void addNpc(final PlayerNpc playerNpc, final MotionCaptureData data);

        boolean removeNpc(final PlayerNpc npc);

        boolean removeNpc(final String name);

        List<PlayerNpc> getNpcs();
    }
}
