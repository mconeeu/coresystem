/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import org.bukkit.entity.Player;

import java.util.List;

public interface HologramManager {

    /**
     * reloads all holograms from database
     */
    void reload();

    void reload(Player p);

    /**
     * adds a temporary Hologram to the Server. This Hologram will not be saved in the core-config and stays until server reload|restart or Hologram-Manager reload
     *
     * @param data hologram packets
     * @return new hologram
     */
    Hologram addHologram(HologramData data);

    /**
     * adds a temporary Hologram to the Server. This Hologram will not be saved in the core-config and stays until server reload|restart or Hologram-Manager reload
     * use /holo or the core-config.json to add Holograms permanently
     *
     * @param data     npc packets
     * @param listMode Choose a Visbility mode and the players that should be on the list (i.e. BLACKLIST with no players means that all players can see the NPC)
     * @param players  players that should be on the list
     * @return new hologram
     */
    Hologram addHologram(HologramData data, ListMode listMode, Player... players);

    /**
     * removes an existing Hologram (if its an permanent Hologram from core-config this is just temporary)
     * if you want to delete NPC from core-config permanently use ingame command /holo remove
     *
     * @param hologram hologram
     */
    void removeHologram(Hologram hologram);

    /**
     * returns the wished Hologram of the given world
     * null if the hologram does not exist in this world
     *
     * @param world target world
     * @param name  database name
     * @return Hologram object
     */
    Hologram getHologram(CoreWorld world, String name);

    /**
     * returns a list of all Holograms on the server
     *
     * @return List of Holograms
     */
    List<Hologram> getHolograms();

}
