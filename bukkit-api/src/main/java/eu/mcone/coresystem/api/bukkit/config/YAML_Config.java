/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.config;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YAML_Config {

    private String pluginName;
    private String fileName;
    private String path;
    @Getter
    private FileConfiguration config;

    public YAML_Config(CorePlugin plugin, String fileName) {
        this.pluginName = plugin.getPluginName();
        this.fileName = fileName;
        File dir = new File("./plugins/" + this.pluginName);
        File file = new File("./plugins/" + this.pluginName, fileName);
        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public YAML_Config(CorePlugin plugin, String path, String fileName) {
        this.pluginName = plugin.getPluginName();
        this.fileName = fileName;
        this.path = path;
        File dir = new File(path + this.pluginName);
        File file = new File(path + this.pluginName, fileName);
        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            if (path != null) {
                this.config.save("./plugins/" + this.path + "/" + this.pluginName + "/" + this.fileName);
            } else {
                this.config.save("./plugins/" + this.pluginName + "/" + this.fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
