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
    @Getter
    private FileConfiguration config;

    public YAML_Config(String plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        File dir = new File("./plugins/" + plugin);
        File file = new File("./plugins/" + plugin, fileName);

        if (!dir.exists()) dir.mkdir();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save("./plugins/" + plugin + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
