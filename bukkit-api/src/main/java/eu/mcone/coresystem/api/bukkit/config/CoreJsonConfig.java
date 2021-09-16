/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
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

import java.io.*;
import java.nio.charset.StandardCharsets;


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
     *
     * @param plugin   plugin
     * @param fileName filename i.e. config.json
     */
    public CoreJsonConfig(CorePlugin plugin, String fileName) {
        this(plugin, null, plugin.getDataFolder(), fileName);
    }

    /**
     * construct the JSON config with a custom path
     *
     * @param plugin    plugin
     * @param configDir custom file path
     * @param fileName  filename i.e config.json
     */
    public CoreJsonConfig(CorePlugin plugin, File configDir, String fileName) {
        this(plugin, null, configDir, fileName);
    }

    /**
     * construct a JSON config from the 'plugins/plugin-name' directory
     *
     * @param plugin   plugin
     * @param tClass   the class to|from where this json config is (de-)serializable (might be null)
     * @param fileName filename i.e. config.json
     */
    public CoreJsonConfig(CorePlugin plugin, Class<T> tClass, String fileName) {
        this(plugin, tClass, plugin.getDataFolder(), fileName);
    }

    /**
     * construct the JSON config with a custom path
     *
     * @param plugin    plugin
     * @param tClass    the class to|from where this json config is (de-)serializable (might be null)
     * @param configDir custom file path
     * @param fileName  filename i.e config.json
     */
    public CoreJsonConfig(CorePlugin plugin, Class<T> tClass, File configDir, String fileName) {
        this.plugin = plugin;
        this.tClass = tClass;

        if (!configDir.exists()) {
            configDir.mkdir();
        }

        this.file = new File(configDir, fileName);
        try {
            if (!file.exists()) {
                this.file.createNewFile();
                try {
                    updateConfig(tClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    FileUtils.writeStringToFile(file, "{}");
                }
            }

            reloadFile();
            plugin.sendConsoleMessage("§aLoaded Config §f" + fileName);
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cCould not load Config " + fileName + ": §f" + e.getMessage());
            throw new IllegalArgumentException("Could not load Config " + fileName, e);
        }
    }

    /**
     * reloads the json packets from file
     * Attention! this overrides all existing changes on the CoreJsonConfig#getJson() object
     *
     * @throws CoreConfigException thrown if an IOException occurs
     */
    public void reloadFile() throws CoreConfigException {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            json = CoreSystem.getInstance().getJsonParser().parse(reader);

            reader.close();
            fis.close();
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cError reloading Config §f" + file.getName());
            throw new CoreConfigException("Cannot reload JsonConfig " + file.getName(), e);
        }
    }

    /**
     * saves the Config to file
     */
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer fw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            PRETTY_GSON.toJson(json, fw);

            fw.close();
            fos.close();
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cError saving Config §f" + file.getName());
            throw new CoreConfigException("Cannot save JsonConfig " + file.getName(), e);
        }
    }

    /**
     * parses the json object to the given Type. Only works if a type was set via constructor
     *
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
     *
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