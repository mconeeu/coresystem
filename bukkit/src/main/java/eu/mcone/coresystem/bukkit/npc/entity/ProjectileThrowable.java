package eu.mcone.coresystem.bukkit.npc.entity;

import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.data.AbstractNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public abstract class ProjectileThrowable<T extends AbstractNpcData> extends CoreNPC<T> implements eu.mcone.coresystem.api.bukkit.npc.entity.ProjectileThrowable {

    protected ProjectileThrowable(Class<T> dataClass, NpcData data, ListMode listMode, Player... players) {
        super(dataClass, data, listMode, players);
    }

    public void throwProjectile(EntityProjectile type) {
        throwProjectile(type, location.getDirection().clone().multiply(1.5));
    }

    public void throwProjectile(EntityProjectile type, Vector vector) {
        Location face = new Location(location.getWorld(), location.getX(), location.getY() + 1.5, location.getZ());

        try {
            Entity entity = type.getClazz().asSubclass(Entity.class).getConstructor(World.class).newInstance(((CraftWorld) face.getWorld()).getHandle());
            entity.setPosition(face.getX(), face.getY(), face.getZ());
            ((CraftWorld) face.getWorld()).getHandle().addEntity(entity);
            entity.getBukkitEntity().setVelocity(vector);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
