/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.mcone.coresystem.lib.mysql.MySQL_Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class LocationFactory {

    public static void updateConfigLocation(Location loc, MySQL_Config config, String key){
        config.updateMySQLConfig(key,
            "[" +
                "\"" + loc.getWorld().getName() + "\"," +
                "\"" + loc.getX() + "\"," +
                "\"" + loc.getY() + "\"," +
                "\"" + loc.getZ() + "\"," +
                "\"" + loc.getYaw() + "\"," +
                "\"" + loc.getPitch() + "\"" +
            "]"
        );

        config.store();
    }

    public static String getJSONLocation(Location loc){
        return
            "[" +
                "\"" + loc.getWorld().getName() + "\"," +
                "\"" + loc.getX() + "\"," +
                "\"" + loc.getY() + "\"," +
                "\"" + loc.getZ() + "\"," +
                "\"" + loc.getYaw() + "\"," +
                "\"" + loc.getPitch() + "\"" +
            "]";
    }

    public static Location getConfigLocation(MySQL_Config config, String key){
        String configVal = config.getConfigValue(key);
        List<String> list = new Gson().fromJson(configVal, new TypeToken<List<String>>() {}.getType());

        boolean valid = true;

        if (list != null) {
            for (String loc : list) {
                if ((loc == null) || (loc.equals("")) || (loc.equals("0"))) {
                    valid = false;
                }
            }
        } else {
            valid = false;
        }

        if(valid) {
            String[] loc = list.toArray(new String[list.size()]);

            String w = loc[0];
            double x = Double.valueOf(loc[1]);
            double y = Double.valueOf(loc[2]);
            double z = Double.valueOf(loc[3]);
            double yaw = Double.valueOf(loc[4]);
            double pitch = Double.valueOf(loc[5]);

            Location location = new Location(Bukkit.getWorld(w), x, y, z);
            location.setYaw((float) yaw);
            location.setPitch((float) pitch);

            return location;
        } else {
            return null;
        }
    }

    public static Location getLocationfromJSON(String json){
        List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());

        boolean valid = true;

        for (String loc : list) {
            if ((loc == null) || (loc.equals("")) || (loc.equals("0"))) {
                valid = false;
            }
        }

        if(valid) {
            String[] loc = list.toArray(new String[list.size()]);

            String w = loc[0];
            double x = Double.valueOf(loc[1]);
            double y = Double.valueOf(loc[2]);
            double z = Double.valueOf(loc[3]);
            double yaw = Double.valueOf(loc[4]);
            double pitch = Double.valueOf(loc[5]);

            Location location = new Location(Bukkit.getWorld(w), x, y, z);
            location.setYaw((float) yaw);
            location.setPitch((float) pitch);

            return location;
        } else {
            return null;
        }
    }

    public static Location getXYZConfigLocation(MySQL_Config config, String key){
        String configVal = config.getConfigValue(key);
        List<String> list = new Gson().fromJson(configVal, new TypeToken<List<String>>() {}.getType());

        boolean valid = true;

        for (String loc : list) {
            if ((loc == null) || (loc.equals("")) || (loc.equals("0"))) {
                valid = false;
            }
        }

        if(valid) {
            String[] loc = list.toArray(new String[list.size()]);

            String w = loc[0];
            double x = Double.valueOf(loc[1]);
            double y = Double.valueOf(loc[2]);
            double z = Double.valueOf(loc[3]);

            return new Location(Bukkit.getWorld(w), x, y, z);
        } else {
            return null;
        }
    }

}
