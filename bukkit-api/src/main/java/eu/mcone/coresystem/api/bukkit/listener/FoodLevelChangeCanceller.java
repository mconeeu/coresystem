package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeCanceller implements Listener {

    private final EntityType[] entityTypes;

    public FoodLevelChangeCanceller(EntityType... entityTypes) {
        this.entityTypes = entityTypes;
    }

    @EventHandler
    public void on(FoodLevelChangeEvent e) {
        if (entityTypes.length > 0) {
            for (EntityType entityType : entityTypes) {
                if (e.getEntityType().equals(entityType)) {
                    e.setCancelled(true);
                    return;
                }
            }
        } else {
            e.setCancelled(true);
        }
    }

}
