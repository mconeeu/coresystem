package eu.mcone.coresystem.api.bukkit.npc.capture;

import java.util.List;

public interface MotionCaptureHandler {

    void loadDatabase();

    boolean saveMotionCapture(final String name, final MotionRecorder recorder);

    MotionCaptureData getMotionCapture(final String name);

    void deleteMotionCapture(final MotionCaptureData data);

    void deleteMotionCapture(final String name);

    List<MotionCaptureData> getMotionCaptures();
}
