package eu.mcone.coresystem.api.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class PlayerBuildCanceller implements Listener {

    private final BuildSystem.BuildEvent[] buildEvents;

    public PlayerBuildCanceller(BuildSystem.BuildEvent... buildEvents) {
        this.buildEvents = buildEvents;
    }

    @EventHandler
    public void on(BlockPlaceEvent e) {
        cancel(e, BuildSystem.BuildEvent.BLOCK_PLACE);
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        cancel(e, BuildSystem.BuildEvent.BLOCK_BREAK);
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        cancel(e, BuildSystem.BuildEvent.INTERACT);
    }

    private void cancel(Cancellable e, BuildSystem.BuildEvent buildEvent) {
        if (buildEvents.length > 0) {
            if (Arrays.asList(buildEvents).contains(buildEvent)) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

}
