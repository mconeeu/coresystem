/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class PlayerDataProfile extends GameProfile {

    @Getter
    private static World gameProfileWorld = Bukkit.getWorlds().get(0);

    private boolean flying;
    private String world;
    private Location location;
    private Map<String, Location> homes;
    int level, foodLevel;
    float exp;
    double health;
    long lastLogin;

    public PlayerDataProfile(Player p, Map<String, Location> homes) {
        this(p);
        this.homes = homes;
    }

    public PlayerDataProfile(Player p) {
        super(p);

        flying = p.isFlying();
        world = p.getWorld().getName();
        location = p.getLocation();
        level = p.getLevel();
        foodLevel = p.getFoodLevel();
        exp = p.getExp();
        health = p.getHealth();
        lastLogin = System.currentTimeMillis() / 1000;
    }

    public PlayerDataProfile() {
        world = gameProfileWorld.getName();
        location = CoreSystem.getInstance().getWorldManager().getWorld(gameProfileWorld).getLocation("spawn");
        foodLevel = 20;
        health = 20D;
        homes = new HashMap<>();
    }

    public void doSetData(Player p) {
        p.teleport(location);
        p.setFlying(flying);
        p.setLevel(level);
        p.setFoodLevel(foodLevel);
        p.setExp(exp);
        p.setHealth(health);
    }

    public static void doSetGameProfileWorld(World world) {
        gameProfileWorld = world;
    }

}
