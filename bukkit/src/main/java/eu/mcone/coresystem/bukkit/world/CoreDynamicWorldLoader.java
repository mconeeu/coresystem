package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.DynamicWorldLoader;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CoreDynamicWorldLoader implements DynamicWorldLoader, Runnable {

    private static final int MAX_UNUSED_TIME = 2 * 60;

    private final BukkitCoreSystem system;
    private final WorldManager manager;
    private final Map<CoreWorld, Integer> unusedMaps;
    @Getter
    private final Set<CoreWorld> blacklist;
    private final BukkitTask task;

    public CoreDynamicWorldLoader(BukkitCoreSystem system, WorldManager manager) {
        this.system = system;
        this.manager = manager;
        this.unusedMaps = new HashMap<>();
        blacklist = new HashSet<>();

        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(system, this, 30 * 60, 20 * 10);
    }

    @Override
    public void run() {
        for (CoreWorld w : manager.getWorlds()) {
            World bw = w.bukkit();

            if (bw != null && !Bukkit.getWorlds().get(0).equals(bw) && bw.getPlayers().size() == 0) {
                int unusedTime = unusedMaps.getOrDefault(w, 0);
                unusedTime += 10;
                unusedMaps.put(w, unusedTime);

                if (unusedTime >= MAX_UNUSED_TIME && w.isLoaded()) {
                    Bukkit.getScheduler().runTask(system, () -> {
                        w.unload(true);
                    });
                }
            }
        }
    }

    @Override
    public boolean addToBlacklist(CoreWorld... world) {
        return blacklist.addAll(Arrays.asList(world));
    }

    @Override
    public boolean removeFromBlacklist(CoreWorld world) {
        return blacklist.remove(world);
    }

    @Override
    public boolean isBlacklisted(CoreWorld world) {
        return blacklist.contains(world);
    }

    public void resetTimer(CoreWorld w) {
        unusedMaps.put(w, 0);
    }

    public void disable() {
        task.cancel();
    }

}
