/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile.interfaces;

import org.bukkit.Location;

import java.util.Map;

/**
 * Implement this Inventory into yout Player class that gets home packets from GameProfileClass
 */
public interface HomeManager {

    /**
     * returns the location for a home with a specific name
     *
     * @param name home name
     * @return home Location
     */
    Location getHome(String name);

    /**
     * returns all Homes of the player (set with /sethome <home> or /home set <home>
     *
     * @return all homes with name and location in a map
     */
    Map<String, Location> getHomes();

    /**
     * adds a new home or updates an existing home
     *
     * @param name name of the home
     * @param location location
     */
    void setHome(String name, Location location);

    /**
     * adds a new home or updates an existing home without saving it to the database
     *
     * @param name name of the home
     * @param location location
     */
    void setHomeLocally(String name, Location location);

    /**
     * removes an existing home
     *
     * @param name name of the home
     * @return true if home was removed, false if there was no home with this name
     */
    boolean removeHome(String name);

    /**
     * removes an existing home without saving it to the database
     *
     * @param name name of the home
     * @return true if home was removed, false if there was no home with this name
     */
    boolean removeHomeLocally(String name);
}
