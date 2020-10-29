package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBedEnterCanceller implements Listener {

    @EventHandler
    public void on(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }

}
