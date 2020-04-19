package eu.mcone.coresystem.api.bukkit.npc.entity;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.entity.*;

@Getter
public enum EntityProjectile {

    SNOWBALL(61, EntityType.SNOWBALL, EntitySnowball.class),
    EGG(62, EntityType.EGG, EntityEgg.class),
    FIRE_BALL(63, EntityType.FIREBALL, EntitySmallFireball.class),
    ENDER_PEARL(65, EntityType.ENDER_PEARL, EntityEnderPearl.class),
    ENDER_SIGNAL(72, EntityType.ENDER_SIGNAL, EntityEnderSignal.class),
    WITHER_SKULL(66, EntityType.WITHER_SKULL, EntityWitherSkull.class),
    THROWEN_EXP_BOTTLE(73, EntityType.THROWN_EXP_BOTTLE, EntityThrownExpBottle.class);

    private final int id;
    private final EntityType type;
    private final Class<? extends Entity> clazz;

    EntityProjectile(int id, EntityType type, Class<? extends Entity> clazz) {
        this.id = id;
        this.type = type;
        this.clazz = clazz;
    }

    public static EntityProjectile getID(EntityType type) {
        for (EntityProjectile projectile : values()) {
            if (projectile.getType().equals(type)) {
                return projectile;
            }
        }

        return null;
    }
}
