/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import org.bukkit.Location;

import java.util.List;

public interface HologramManager {

    /**
     * reloads all holograms from database
     */
    void reload();

    /**
     * adds a temporary Hologram to the Server. This Hologram will not be saved in the core-config and stays until server reload|restart or Hologram-Manager reload
     * use /holo or the core-config.json to add Holograms permanently
     * @param name config name
     * @param location location
     * @param text content of hologram
     */
    Hologram addHologram(String name, Location location, String... text);

    /**
     * removes an existing Hologram (if its an permanent Hologram from core-config this is just temporary)
     * if you want to delete NPC from core-config permanently use ingame command /holo remove
     * @param hologram hologram
     */
    void removeHologram(Hologram hologram);

    /**
     * returns the wished Hologram of the given world
     * null if the hologram does not exist in this world
     * @param world target world
     * @param name database name
     * @return Hologram object
     */
    Hologram getHologram(CoreWorld world, String name);

    /**
     * returns a list of all Holograms on the server
     * @return List of Holograms
     */
    List<Hologram> getHolograms();

    /**
     * update all hologram to all players
     */
    void updateHolograms();

}
