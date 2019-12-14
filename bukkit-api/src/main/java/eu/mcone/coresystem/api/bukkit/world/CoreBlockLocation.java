/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class CoreBlockLocation {

    private transient Location bukkit;

    @Getter
    private String world;
    @Getter
    private int x, y, z;

    public CoreBlockLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = (int) location.getX();
        this.y = (int) location.getY();
        this.z = (int) location.getZ();
    }

    public Location bukkit() {
        return bukkit != null ? bukkit : (bukkit = new Location(Bukkit.getWorld(world), x, y, z));
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
        return "CoreBlockLocation{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
