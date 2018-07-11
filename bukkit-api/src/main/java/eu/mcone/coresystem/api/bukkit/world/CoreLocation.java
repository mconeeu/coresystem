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

    /**
     * construct new CoreLocation from JSON String
     * @param json JSON String
     * @return new CoreLocation
     */
    public static CoreLocation fromJson(String json) {
        return CoreSystem.getInstance().getGson().fromJson(json, CoreLocation.class);
    }

    /**
     * get CoreLocation as JSON String for example for putting it into database
     * @return JSON String
     */
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

    @Override
    public String toString() {
        return "world="+worldName+", x="+x+", y="+y+", z="+z+", yaw="+yaw+", pitch="+pitch;
    }

}
