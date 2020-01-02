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
