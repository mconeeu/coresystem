package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class WorldGrowCanceller implements Listener {

    @EventHandler
    public void on(BlockPhysicsEvent e) {
        switch (e.getBlock().getType()) {
            case GRASS:
            case DIRT:
            case FIRE: {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(BlockDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(BlockGrowEvent e) {
        e.setCancelled(true);
    }

}
