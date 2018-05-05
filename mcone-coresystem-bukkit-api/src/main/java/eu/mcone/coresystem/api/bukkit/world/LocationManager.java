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

    public abstract LocationManager registerLocation(String name);

    public abstract LocationManager downloadLocations();

    public abstract LocationManager preventSpawnCommand();

    public abstract boolean putLocation(String name, Location location);

    public abstract Location getLocation(String name);

    public abstract void teleport(Player player, String name);

    public abstract void teleportSilently(Player p, String name);

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
