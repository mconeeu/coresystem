/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class WorldLocation implements eu.mcone.coresystem.api.bukkit.world.WorldLocation {

    private String MainPrefix = "§8[§fWorldLocation§8] ";

    private String directory_name;
    private String fileName;
    private String world;

    private File dir;
    private File file;

    @Getter
    private FileConfiguration config;

    @Getter
    private boolean useJson;

    /**
     * Default WorldLocation constructor.
     * @param world Specifies the world name of the server.
     * @param directory_name Specifies the name of the folder.
     * @param fileName Specifies the name of output file.
     * @param useJson Specifies whether the Json object should be used
     */
    public WorldLocation(final String world, final String directory_name, final String fileName, final boolean useJson) {
        this.useJson = useJson;
        this.world = world;
        this.fileName = fileName;

        if(!useJson) {
            this.directory_name = directory_name + "/normal";

            this.dir = new File("./" + world + "/" + directory_name + "/normal");
            this.file = new File("./" + world + "/" + directory_name + "/normal", fileName);
        } else {
            this.directory_name = directory_name + "/json";

            this.dir = new File("./" + world + "/" + directory_name + "/json");
            this.file = new File("./" + world + "/" + directory_name + "/json", fileName);
        }
    }

    /**
     * Creates the output directory for the location file.
     */
    public void createDirectory() {
        Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§aCreate directory and config...");

        if (!dir.exists() && !file.exists()) {
            try {
                dir.mkdirs();
                file.createNewFile();
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * @param key Define a key for the config values.
     * @param location Specifies the location to be saved in the location file.
     */
    public void addLocation(String key, Location location) {
        if(!useJson) {
            Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§aPaste the location into the config...");
            config.options().copyDefaults(true);
            config.addDefault(key + ".World", location.getWorld().getName());
            config.addDefault(key + ".X", location.getX());
            config.addDefault(key + ".Y", location.getY());
            config.addDefault(key + ".Z", location.getZ());
            config.addDefault(key + ".YAW", location.getYaw());
            config.addDefault(key + ".PITCH", location.getPitch());
            save();
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§aPaste the location as json formats into the config...");
            String json = toJson(location);
            config.options().copyDefaults(true);
            config.addDefault(key, json);
            save();
        }
    }

    /**
     * @param key
     * @return Returns the location where the key.
     */
    public Location getLocation(String key) {
        if(config.getString(key ) != null) {
            if(!useJson) {
                Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§aReturns the location...");
                World world = Bukkit.getWorld(config.getString(key + ".World"));
                double X = config.getDouble(key + ".X");
                double Y = config.getDouble(key + ".Y");
                double Z = config.getDouble(key + ".Z");

                float YAW = (float) this.getConfig().getDouble(key + ".YAW");
                float PITCH = (float) this.getConfig().getDouble(key + ".PITCH");

                return new Location(world, X, Y, Z, YAW, PITCH);
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§aReturns the location as Json format...");
                String json = config.getString(key);
                Location location = fromJsonAsLocation(json);
                return location;
            }
        } else {
            throw new NullPointerException("[WorldLocation] No values could be found for the key '" + key + "'");
        }
    }

    /**
     * Saved the location file.
     */
    private void save() {
        try {
            if (dir.exists() && file.exists()) {
                this.config.save(dir + "/" + fileName);
                Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§aSave Location config...");
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§cThe directory or the file doesn't exists..");
            }
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§cError can't not save the file...");
            e.printStackTrace();
        }
    }

    public String fromJsonAsString(String key) {
        if(useJson) {
            if(config.getString(key ) != null) {
                return config.getString(key);
            } else {
                throw new NullPointerException("[WorldLocation] No values could be found for the key '" + key + "'");
            }
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(MainPrefix + "§cYou not use Json...");
            return "ERROR";
        }
    }

    private String toJson(Location location) {
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

    private Location fromJsonAsLocation(String json) {
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