package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageCanceller implements Listener {

    private final EntityDamageEvent.DamageCause[] causes;

    public EntityDamageCanceller(EntityDamageEvent.DamageCause... causes) {
        this.causes = causes;
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        if (causes.length > 0) {
            for (EntityDamageEvent.DamageCause cause : causes) {
                if (e.getCause().equals(cause)) {
                    e.setCancelled(true);
                    return;
                }
            }
        } else {
            e.setCancelled(true);
        }
    }

}
