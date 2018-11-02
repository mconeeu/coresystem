/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@NoArgsConstructor
@Getter @Setter
public class PlayerDataProfile extends PlayerInventoryProfile {

    @Getter
    private static World gameProfileWorld = Bukkit.getWorlds().get(0);

    private boolean flying;
    private String world = gameProfileWorld.getName();
    private CoreLocation location = CoreSystem.getInstance().getWorldManager().getWorld(gameProfileWorld).getLocation("spawn");
    int level, foodLevel = 20;
    float exp;
    double health = 20D;

    public PlayerDataProfile(Player p) {
        super(p);

        flying = p.isFlying();
        world = p.getWorld().getName();
        location = new CoreLocation(p.getLocation());
        level = p.getLevel();
        foodLevel = p.getFoodLevel();
        exp = p.getExp();
        health = p.getHealth();
    }

    public void doSetData(Player p) {
        p.teleport(calculateBukkitLocation());
        p.setFlying(flying);
        p.setLevel(level);
        p.setFoodLevel(foodLevel);
        p.setExp(exp);
        p.setHealth(health);

        doSetItemInventory(p);
    }

    public Location calculateBukkitLocation() {
        return new Location(Bukkit.getWorld(world), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static void doSetGameProfileWorld(World world) {
        gameProfileWorld = world;
    }

}
