/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@AllArgsConstructor
@Getter @Setter
public final class CoreLocation {

    private String worldName;
    private double x, y, z;
    private float yaw, pitch;

    public CoreLocation(Location loc) {
        this(
                loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch()
        );
    }

    public static CoreLocation fromJson(String json) {
        return CoreSystem.getInstance().getGson().fromJson(json, CoreLocation.class);
    }

    public String toJson() {
        return CoreSystem.getInstance().getGson().toJson(this, getClass());
    }

    /**
     * get Bukkit Location object
     * @return Bukkit Location
     */
    public Location bukkit() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

}
