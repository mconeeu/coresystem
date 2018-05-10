/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import org.bukkit.Location;

public interface HologramManager {

    /**
     * reloads all holograms from database
     */
    void reload();

    /**
     * adds a new hologram
     * @param name data name
     * @param location location
     * @param line1 fist line
     */
    void addHologram(String name, Location location, String line1);

    /**
     * deletes an hologram
     * @param name data name
     */
    void removeHologram(String name);

    /**
     * update all hologram to all players
     */
    void updateHolograms();

}
