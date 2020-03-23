package eu.mcone.coresystem.api.bukkit.npc.entity;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import org.bukkit.util.Vector;

public interface ProjectileThrowable extends NPC {

    void throwProjectile(EntityProjectile type);

    void throwProjectile(EntityProjectile type, Vector vector);

}
