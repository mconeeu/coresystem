/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

@Deprecated
public interface WorldLocation {

    void createDirectory();

    void addLocation(String key, Location location);

    Location getLocation(String key);

    String fromJsonAsString(String key);

    boolean isUseJson();

    FileConfiguration getConfig();

}
