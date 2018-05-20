/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YAML_Config {

    private String plugin;
    private String fileName;
    private String path;
    private String subdirectory;
    @Getter
    private FileConfiguration config;

    private boolean usePath = false;
    private boolean hasSubdirectory = false;

    public YAML_Config(String plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        File dir = new File("./plugins/" + plugin);
        File file = new File("./plugins/" + plugin, fileName);
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

    public YAML_Config(String plugin, String path, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.path = path;
        this.usePath = true;
        File dir = new File(path + plugin);
        File file = new File(path + plugin, fileName);
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

    public YAML_Config(String plugin, String path, String subdirectory,  String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.path = path;
        this.subdirectory = subdirectory;

        this.hasSubdirectory = true;
        this.usePath = true;
        File dir = new File(path + plugin + subdirectory);
        File file = new File(path + plugin + subdirectory, fileName);
        if (!dir.exists()) {
            dir.mkdirs();
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
            if(this.usePath) {
                this.config.save("./" + this.path + "/" + this.plugin + "/" + this.fileName);
            }

            if(this.hasSubdirectory) {
                this.config.save("./" + this.path + "/" + this.plugin + "/" + this.subdirectory + "/" + this.fileName);
            } else {
                this.config.save("./plugins/" + this.plugin + "/" + this.fileName);
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public FileConfiguration getConfig() {
        return this.config;
    }

}
