package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketWrapper;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MotionRecorder implements Listener, Serializable {

    private static final long serialVersionUID = 191955L;

    @Getter
    protected int ticks;
    @Getter
    private String recorderName;
    @Getter
    private String world;
    @Getter
    private String name;
    @Getter
    protected long recorded;
    @Getter
    protected boolean isStopped = false;

    protected transient BukkitTask taskID;
    protected transient Player player;
    protected AtomicInteger savedPackets;

    @Getter
    public HashMap<String, List<PacketWrapper>> packets;

    public MotionRecorder(final Player player, final String name) {
        this.player = player;
        this.name = name;
        this.recorderName = player.getName();
        this.world = player.getLocation().getWorld().getName();
        this.savedPackets = new AtomicInteger();
        packets = new HashMap<>();
    }

    public abstract void record();

    protected void addData(PacketWrapper data) {
        String tick = String.valueOf(ticks);
        if (this.packets.containsKey(tick)) {
            this.packets.get(tick).add(data);
        } else {
            this.packets.put(tick, new ArrayList<PacketWrapper>() {{
                add(data);
            }});
        }

        savedPackets.getAndIncrement();
    }

    public abstract Map<String, List<PacketWrapper>> stopRecording();
}
