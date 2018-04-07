/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.command.SpawnCMD;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

public class LocationManager {

    private Map<String, Location> locations;
    private String server;
    @Getter
    private boolean allowSpawnCMD;

    public LocationManager(String server) {
        this.locations = new HashMap<>();
        this.server = server;
        this.allowSpawnCMD = true;

        locations.put("spawn", null);
        CoreSystem.getInstance().getCommand("spawn").setExecutor(new SpawnCMD(this));
    }

    public LocationManager registerLocation(String name) {
        locations.put(name, null);
        return this;
    }

    public LocationManager downloadLocations() {
        CoreSystem.mysql1.select("SELECT * FROM `bukkitsystem_locations` WHERE `server`='"+server+"'", rs -> {
            try {
                while (rs.next()) {
                    String name = rs.getString("name");

                    if (locations.containsKey(name)) {
                        locations.put(name, fromJson(rs.getString("location")));
                    } else {
                        CoreSystem.mysql1.update("DELETE FROM `bukkitsystem_locations` WHERE `id`='"+rs.getInt("id")+"'");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    public LocationManager preventSpawnCommand() {
        allowSpawnCMD = false;
        return this;
    }

    public boolean putLocation(String name, Location location) {
        if (locations.containsKey(name)) {
            if ((boolean) CoreSystem.mysql1.select("SELECT `id` FROM `bukkitsystem_locations` WHERE `name`='' AND `server`=''", rs -> {
                try {
                    if (rs.next()) {
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            })) {
                CoreSystem.mysql1.update("UPDATE `bukkitsystem_locations` SET `location`='" + toJson(location) + "' WHERE `name`='" + name + "' AND `server`='" + server + "'");
            } else {
                CoreSystem.mysql1.update("INSERT INTO `bukkitsystem_locations` (`name`, `location`, `server`) VALUES ('"+name+"', '"+ toJson(location)+"', '"+server+"')");
            }

            locations.put(name, location);
            return true;
        }
        return false;
    }

    public Location getLocation(String spawn) {
        return locations.getOrDefault(spawn, null);
    }

    public void teleport(Player p, String name) {
        Location loc = getLocation(name);

        if (loc != null) {
            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§2Du wirst teleportiert...");
            p.teleport(loc);
        } else {
            p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Dieser Ort existiert nicht.");
        }
    }

    public void teleportSilently(Player p, String name) {
        Location loc = getLocation(name);

        if (loc != null) {
            p.teleport(loc);
        }
    }

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
