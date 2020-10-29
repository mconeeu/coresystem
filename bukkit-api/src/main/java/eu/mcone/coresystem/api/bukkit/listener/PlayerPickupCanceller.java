package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupCanceller implements Listener {

    @EventHandler
    public void on(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

}
