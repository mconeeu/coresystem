package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.capture.packets.PacketWrapper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MotionRecorder implements Serializable, Listener {

    private static final long serialVersionUID = 191955L;

    @Getter
    private int ticks;
    @Getter
    private final String recorderName;
    @Getter
    private final String world;
    @Getter
    private long recorded;
    @Getter
    private boolean isStopped = false;

    protected transient BukkitTask taskID;
    protected transient final Player player;
    private AtomicInteger savedPackets;

    @Getter
    public HashMap<Integer, List<PacketWrapper>> packets = new HashMap<>();

    public MotionRecorder(final Player player) {
        this.player = player;
        this.recorderName = player.getName();
        this.world = player.getLocation().getWorld().getName();
        this.savedPackets = new AtomicInteger();
    }

    public void record() {
        recorded = System.currentTimeMillis() / 1000;

        taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.getInstance().createActionBar().message("§2§lAufnahme §8│ §a§l" + savedPackets.get() + " §2packet(s)").send(player);
            ticks++;
        }, 1L, 1L);

    }

    public Map<Integer, List<PacketWrapper>> stopRecording() {
        isStopped = true;
        return packets;
    }

    protected void addData(PacketWrapper data) {
        if (this.packets.containsKey(ticks)) {
            this.packets.get(ticks).add(data);
        } else {
            this.packets.put(ticks, new ArrayList<PacketWrapper>() {{
                add(data);
            }});
        }

        savedPackets.getAndIncrement();
    }
}
