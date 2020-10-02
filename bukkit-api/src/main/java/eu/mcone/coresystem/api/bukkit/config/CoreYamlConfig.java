/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.core.exception.CoreConfigException;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CoreYamlConfig {

    private final CorePlugin plugin;
    private final File file;
    @Getter
    private final FileConfiguration config;

    /**
     * construct a YAML config from the 'plugins/plugin-name' directory
     * @param plugin plugin
     * @param fileName filename i.e. config.yml
     */
    public CoreYamlConfig(CorePlugin plugin, String fileName) {
        this(plugin, plugin.getDataFolder(), fileName);
    }

    /**
     * construct the YAML config with a custom path
     * @param plugin plugin
     * @param configDir custom file path
     * @param fileName filename i.e config.yml
     */
    public CoreYamlConfig(CorePlugin plugin, File configDir, String fileName) {
        this.plugin = plugin;

        if (!configDir.exists()) {
            configDir.mkdir();
        }

        this.file = new File(configDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        plugin.sendConsoleMessage("§aLoaded Config §f"+fileName);
    }

    /**
     * saves the Config to file
     * @throws CoreConfigException thrown if an IOException occurs
     */
    public void save() throws CoreConfigException {
        try {
            this.config.save(file);
        } catch (IOException e) {
            plugin.sendConsoleMessage("§cError reloading Config §f" + file.getName());
            throw new CoreConfigException("Cannot reload YamlConfig "+file.getName(), e);
        }
    }

}
