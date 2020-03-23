package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public interface CoreProjectile {

    CoreProjectile type(EntityProjectile projectile);

    CoreProjectile velocity(Vector vector);

    CoreProjectile throwProjectile(Location location);

}
