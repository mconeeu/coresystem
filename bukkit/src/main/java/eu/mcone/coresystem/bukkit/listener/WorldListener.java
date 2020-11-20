package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.world.RegionEnterEvent;
import eu.mcone.coresystem.api.bukkit.event.world.RegionQuitEvent;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.Region;
import eu.mcone.coresystem.bukkit.world.WorldManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
public class WorldListener implements Listener {

    private final WorldManager manager;

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            if (shouldMoveBeCancelled(p, e.getFrom(), e.getTo())) {
                p.teleport(e.getFrom());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTp(PlayerTeleportEvent e) {
        e.setCancelled(shouldMoveBeCancelled(e.getPlayer(), e.getFrom(), e.getTo()));
    }

    private boolean shouldMoveBeCancelled(Player p, Location from, Location to) {
        CoreWorld w = manager.getWorld(p.getWorld());

        if (w.getRegions().size() > 0) {
            for (Region region : w.getRegions()) {
                if (region.isInRegion(from) && !region.isInRegion(to)) {
                    RegionQuitEvent event = new RegionQuitEvent(p, w, region);
                    Bukkit.getPluginManager().callEvent(event);

                    return event.isCancelled();
                } else if (!region.isInRegion(from) && region.isInRegion(to)) {
                    RegionEnterEvent event = new RegionEnterEvent(p, w, region);
                    Bukkit.getPluginManager().callEvent(event);

                    return event.isCancelled();
                }
            }
        }

        return false;
    }

}
