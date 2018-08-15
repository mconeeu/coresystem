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
import org.bukkit.Location;
import org.bukkit.World;

@AllArgsConstructor
@Getter @Setter
public final class CoreLocation {

    private double x, y, z;
    private float yaw, pitch;

    public CoreLocation(Location loc) {
        this(
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
     * get Bukkit Location object for the given world-location combination
     * @param world CoreWorld object
     * @return Bukkit Location
     */
    public Location bukkit(CoreWorld world) {
        return new Location(world.bukkit(), x, y, z, yaw, pitch);
    }

    /**
     * get Bukkit Location object for the given world-location combination
     * @param world Bukkit world object
     * @return Bukkit Location
     */
    public Location bukkit(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "x="+x+", y="+y+", z="+z+", yaw="+yaw+", pitch="+pitch;
    }

}
