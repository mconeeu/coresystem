/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HologramManager {

    void reload();

    void addHologram(String name, Location location, String line1);

    void removeHologram(String name);

    void updateHolograms();

    void setHolograms(Player player);

    void unsetHolograms();

}
