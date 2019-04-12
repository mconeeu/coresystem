/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@NoArgsConstructor
public final class CoreLocation {

    private transient Location bukkit;

    @Getter
    private String world;
    @Getter
    private double x, y, z;
    @Getter
    private float yaw, pitch;

    public CoreLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public CoreLocation(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public Location bukkit() {
        return bukkit != null ? bukkit : (bukkit = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    @Override
    public String toString() {
        return "Corelocation(world="+world+", x="+Math.round(x)+", y="+Math.round(y)+", z="+Math.round(z)+", yaw="+Math.round(yaw)+", pitch="+Math.round(pitch)+")";
    }
}
