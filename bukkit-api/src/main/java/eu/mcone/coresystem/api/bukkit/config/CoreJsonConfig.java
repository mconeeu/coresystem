/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.gson.CraftItemStackTypeAdapter;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.gson.LocationTypeAdapter;
import eu.mcone.coresystem.api.core.exception.CoreConfigException;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class CoreJsonConfig<T> {

    public final static Gson PRETTY_GSON = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeAdapter(ItemStack.class, new CraftItemStackTypeAdapter())
            .registerTypeAdapter(CraftItemStack.class, new CraftItemStackTypeAdapter())
            .setPrettyPrinting()
            .create();

    private final CorePlugin plugin;
    private final File file;
    private final Class<T> tClass;

    @Getter
    private JsonElement json;

    /**
     * construct a JSON config from the 'plugins/plugin-name' directory
     * @param plugin plugin
     * @param fileName filename i.e. config.json
     */
    public CoreJsonConfig(CorePlugin plugin, String fileName) {
        this(plugin, null, "./plugins/" + plugin.getPluginName(), fileName);
    }

    /**
     * construct the JSON config with a custom path
     * @param plugin plugin
     * @param path custom file path
     * @param fileName filename i.e config.json
     */
    public CoreJsonConfig(CorePlugin plugin, String path, String fileName) {
        this(plugin, null, path, fileName);
    }

    /**
     * construct a JSON config from the 'plugins/plugin-name' directory
     * @param plugin plugin
     * @param tClass the class to|from where this json config is (de-)serializable (might be null)
     * @param fileName filename i.e. config.json
     */
    public CoreJsonConfig(CorePlugin plugin, Class<T> tClass, String fileName) {
        this(plugin, tClass, "./plugins/" + plugin.getPluginName(), fileName);
    }

    /**
     * construct the JSON config with a custom path
     * @param plugin plugin
     * @param tClass the class to|from where this json config is (de-)serializable (might be null)
     * @param path custom file path
     * @param fileName filename i.e config.json
     */
    public CoreJsonConfig(CorePlugin plugin, Class<T> tClass, String path, String fileName) {
        this.plugin = plugin;
        this.tClass = tClass;

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        this.file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                this.file.createNewFile();
                FileUtils.writeStringToFile(file, "{}");
            }

            reloadFile();
            plugin.sendConsoleMessage("§aLoaded Config §f" + fileName);
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cError loading Config §f" + fileName);
            e.printStackTrace();
        }
    }

    /**
     * reloads the json data from file
     * Attention! this overrides all existing changes on the CoreJsonConfig#getJson() object
     * @throws CoreConfigException thrown if an IOException occurs
     */
    public void reloadFile() throws CoreConfigException {
        try {
            FileReader fr = new FileReader(file);
            json = CoreSystem.getInstance().getJsonParser().parse(fr);

            fr.close();
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cError reloading Config §f" + file.getName());
            throw new CoreConfigException("Cannot reload JsonConfig "+file.getName(), e);
        }
    }

    /**
     * saves the Config to file
     */
    public void save() {
        try {
            FileWriter fw = new FileWriter(file, false);
            PRETTY_GSON.toJson(json, fw);

            fw.close();
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cError saving Config §f" + file.getName());
            throw new CoreConfigException("Cannot save JsonConfig "+file.getName(), e);
        }
    }

    /**
     * parses the json object to the given Type. Only works if a type was set via constructor
     * @return serialized object of given type
     * @throws IllegalStateException thrown if type was not set via constructor
     */
    public T parseConfig() throws IllegalStateException {
        if (tClass != null) {
            return PRETTY_GSON.fromJson(json, tClass);
        } else {
            throw new IllegalStateException("Config must be instantiated with a class if using POJO");
        }
    }

    /**
     * parses an object of a specific type if type was set via constructor
     * @param config serialized object of given type
     * @throws IllegalStateException thrown if type was not set via constructor
     */
    public void updateConfig(T config) throws IllegalStateException {
        if (tClass != null) {
            this.json = PRETTY_GSON.toJsonTree(config, tClass);
            save();
        } else {
            throw new IllegalStateException("Config must be instantiated with a class if using POJO");
        }
    }

}