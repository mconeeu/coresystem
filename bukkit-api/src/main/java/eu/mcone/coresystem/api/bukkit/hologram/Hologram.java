/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Hologram {

    /**
     * returns the Holograms config data
     * @return HologramData config
     */
    HologramData getData();

    /**
     * returns the Holograms CoreWorld
     * @return CoreWorld object
     */
    CoreWorld getWorld();

    /**
     * returns the Holograms location
     * @return Bukkit Location object
     */
    Location getLocation();

    /**
     * shows the hologram a player for a specific time
     * @param player player
     * @param time time int in seconds
     */
    void showPlayerTemp(Player player, int time);

    /**
     * shows the hologram all players for a specific time
     * @param time time int in seconds
     */
    void showAllTemp(int time);

    /**
     * shows the hologram a specific player
     * @param player player
     */
    void showPlayer(Player player);

    /**
     * hides the hologram from a specific player
     * @param player player
     */
    void hidePlayer(Player player);

    /**
     * shows the hologram to all players
     */
    void showAll();

    /**
     * hides the hologram from all players
     */
    void hideAll();

}
