package eu.mcone.coresystem.bukkit.npc.entity;

import eu.mcone.coresystem.bukkit.npc.nms.EntityHumanNPC;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.util.Vector;

public class FishingHook extends EntityFishingHook {

    private final World world;
    private final Location location;
    private final EntityHumanNPC npc;

    public FishingHook(Location location, EntityHumanNPC npc){
        super(((CraftWorld) location.getWorld()).getHandle(), npc);
        this.location = location;
        world = ((CraftWorld) location.getWorld()).getHandle();
        locX = location.getX();
        locY = location.getY();
        locZ = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
        this.npc = npc;
    }

    public void spawn(){
        world.addEntity(this);
        Vector velocity = location.getDirection().multiply(1.5);
        CraftEntity entity = this.getBukkitEntity();
        this.setPosition(locX, locY, locZ);
        entity.setVelocity(velocity);
    }
}
