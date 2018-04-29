/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class WorldLocation implements eu.mcone.coresystem.api.bukkit.world.WorldLocation {

    private String directory_name;
    private String fileName;
    private String world;

    private File dir;
    private File file;

    @Getter
    private FileConfiguration config;

    /**
     * WorldLocation constructor.
     * @param world Specifies the world name of the server.
     * @param directory_name Specifies the name of the folder.
     * @param fileName Specifies the name of output file.
     */
    public WorldLocation(final String world, final String directory_name, final String fileName) {
        this.world = world;
        this.directory_name = directory_name;
        this.fileName = fileName;

        this.dir = new File("./" + world + "/" + directory_name);
        this.file = new File("./" + world + "/" + directory_name, fileName);
    }

    /**
     * Creates the output directory for the location file.
     */
    public void createDirectory() {
        if (!dir.exists()) dir.mkdir();
        if (!file.exists()) {
            try {
                file.createNewFile();
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        System.out.println("Create directory and config...");
    }

    /**
     * @param key Define a key for the config values.
     * @param location Specifies the location to be saved in the location file.
     */
    public void addLocation(String key, Location location) {
        this.config.options().copyDefaults(true);
        this.config.addDefault(key + ".World", location.getWorld().getName());
        this.config.addDefault(key + ".X", location.getX());
        this.config.addDefault(key + ".Y", location.getY());
        this.config.addDefault(key + ".Z", location.getZ());
        this.config.addDefault(key + ".YAW", location.getYaw());
        this.config.addDefault(key + ".PITCH", location.getPitch());
        save();
    }

    /**
     * @param key
     * @return Returns the location where the key.
     */
    public Location getLocation(String key) {
        World world = Bukkit.getWorld(this.getConfig().getString(key + ".World"));
        double X = this.config.getDouble(key + ".X");
        double Y = this.config.getDouble(key + ".X");
        double Z = this.config.getDouble(key + ".X");

        float YAW = (float) this.getConfig().getDouble(key + ".YAW");
        float PITCH = (float) this.getConfig().getDouble(key + ".PITCH");

        return new Location(world, X, Y, Z, YAW, PITCH);
    }

    /**
     * Saved the location file.
     */
    private void save() {
        try {
            if (dir.exists() && file.exists()) {
                this.config.save("./" + world + "/" + directory_name + "/" + fileName);
                System.out.println("Save Location config...");
            } else {
                System.out.println("The directory or the file doesn't exists..");
            }
        } catch (IOException e) {
            System.out.println("Error can't not save the file...");
            e.printStackTrace();
        }
    }
}
