/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.SpawnCMD;
import eu.mcone.coresystem.core.mysql.MySQL;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LocationManager extends eu.mcone.coresystem.api.bukkit.world.LocationManager {

    private final MySQL mySQL;
    private final Map<String, Location> locations;
    private final String server;
    @Getter
    private boolean allowSpawnCMD;

    public LocationManager(MySQL mySQL, String server) {
        this.locations = new HashMap<>();
        this.mySQL = mySQL;
        this.server = server;
        this.allowSpawnCMD = true;

        locations.put("spawn", null);
        BukkitCoreSystem.getInstance().getCommand("spawn").setExecutor(new SpawnCMD(this));
    }

    public LocationManager registerLocation(String name) {
        locations.put(name, null);
        return this;
    }

    public LocationManager downloadLocations() {
        mySQL.select("SELECT * FROM `bukkitsystem_locations` WHERE `server`='"+server+"'", rs -> {
            try {
                while (rs.next()) {
                    String name = rs.getString("name");

                    if (locations.containsKey(name)) {
                        locations.put(name, fromJson(rs.getString("location")));
                    } else {
                        mySQL.update("DELETE FROM `bukkitsystem_locations` WHERE `id`='"+rs.getInt("id")+"'");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return this;
    }

    @Override
    public LocationManager preventSpawnCommand(boolean prevent) {
        allowSpawnCMD = !prevent;
        return this;
    }

    public boolean putLocation(String name, Location location) {
        if (locations.containsKey(name)) {
            if ((boolean) mySQL.select("SELECT `id` FROM `bukkitsystem_locations` WHERE `name`='' AND `server`=''", rs -> {
                try {
                    if (rs.next()) {
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            })) {
                mySQL.update("UPDATE `bukkitsystem_locations` SET `location`='" + toJson(location) + "' WHERE `name`='" + name + "' AND `server`='" + server + "'");
            } else {
                mySQL.update("INSERT INTO `bukkitsystem_locations` (`name`, `location`, `server`) VALUES ('"+name+"', '"+ toJson(location)+"', '"+server+"')");
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
            Messager.send(p, "§2Du wirst teleportiert...");
            p.teleport(loc);
        } else {
            Messager.send(p, "§4Dieser Ort existiert nicht.");
        }
    }

    public void teleportSilently(Player p, String name) {
        Location loc = getLocation(name);

        if (loc != null) {
            p.teleport(loc);
        }
    }



}
