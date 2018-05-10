/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LocationManager {

    /**
     * Preregisters available Location-Names which can be set afterwards via command
     * @param name predefined Location name
     * @return this
     */
    public abstract LocationManager registerLocation(String name);

    /**
     * Downloads Location-Data from database
     * @return this
     */
    public abstract LocationManager downloadLocations();

    /**
     * Prevents players from using the /spawn command
     * @param prevent prevent use of /spawn
     * @return this
     */
    public abstract LocationManager preventSpawnCommand(boolean prevent);

    /**
     * Add a Location an save it to database
     * @param name Preregistered Location name
     * @param location location
     * @return this
     */
    public abstract boolean putLocation(String name, Location location);

    /**
     * get Location by name
     * @param name preregistered Location name
     * @return
     */
    public abstract Location getLocation(String name);

    /**
     * teleport a specific player to a preregistered Location
     * @param player player
     * @param name preregistered location name
     */
    public abstract void teleport(Player player, String name);

    /**
     * teleport a specific player without notifying to a preregistered Location
     * @param player player
     * @param name preregistered location name
     */
    public abstract void teleportSilently(Player player, String name);

    /**
     * convert Location to json String
     * @param location location
     * @return json String
     */
    public static String toJson(Location location) {
        List<String> result = new ArrayList<>(Arrays.asList(
                location.getWorld().getName(),
                String.valueOf(location.getX()),
                String.valueOf(location.getY()),
                String.valueOf(location.getZ()),
                String.valueOf(location.getYaw()),
                String.valueOf(location.getPitch())
        ));

        return new Gson().toJson(result);
    }

    /**
     * convert json String to Location
     * @param json location String
     * @return Location
     */
    public static Location fromJson(String json) {
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();

        return new Location(
                Bukkit.getWorld(array.get(0).getAsString()),
                array.get(1).getAsDouble(),
                array.get(2).getAsDouble(),
                array.get(3).getAsDouble(),
                array.get(4).getAsFloat(),
                array.get(5).getAsFloat()
        );
    }

}
