/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@NoArgsConstructor
@Getter @Setter
public class PlayerDataProfile extends PlayerInventoryProfile {

    public PlayerDataProfile(Player p) {
        super(p);

        flying = p.isFlying();
        world = p.getWorld().getName();
        location = new CoreLocation(p.getLocation());
        level = p.getLevel();
        exp = p.getExp();
        health = p.getHealth();
    }

    private boolean flying;
    private String world;
    private CoreLocation location;
    int level;
    float exp;
    double health;

    public void setData(Player p) {
        p.teleport(getLocation());
        p.setFlying(flying);
        p.setLevel(level);
        p.setExp(exp);
        p.setHealth(health);

        setItemInventory(p);
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

}
