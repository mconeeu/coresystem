package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropCanceller implements Listener {

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

}
