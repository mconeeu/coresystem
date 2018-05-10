/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@AllArgsConstructor
@Getter @Setter
public final class CoreLocation {

    private String name;
    private double x, y, z;
    private float yaw, pitch;

    /**
     * get Bukkit Location object
     * @return Bukkit Location
     */
    public Location bukkit() {
        return new Location(Bukkit.getWorld(name), x, y, z, yaw, pitch);
    }

}
