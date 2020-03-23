package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.util.CoreProjectile;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class Projectile implements CoreProjectile {

    private EntityProjectile type;
    private Vector velocity;

    public Projectile(EntityProjectile type) {
        this.type = type;
    }

    @Override
    public CoreProjectile type(EntityProjectile projectile) {
        this.type = projectile;
        return this;
    }

    @Override
    public CoreProjectile velocity(Vector vector) {
        this.velocity = vector;
        return this;
    }

    @Override
    public CoreProjectile throwProjectile(Location location) {
        Location face = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        velocity = velocity != null ? velocity : location.getDirection().clone().multiply(1.5);

        try {
            Entity entity = type.getClazz().asSubclass(Entity.class).getConstructor(World.class).newInstance(((CraftWorld) face.getWorld()).getHandle());
            entity.setPosition(face.getX(), face.getY(), face.getZ());
            ((CraftWorld) face.getWorld()).getHandle().addEntity(entity);
            entity.getBukkitEntity().setVelocity(velocity);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return this;
    }

}
