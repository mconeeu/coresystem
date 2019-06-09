/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.spawnable.PlayerListModeToggleable;

public interface Hologram extends PlayerListModeToggleable {

    /**
     * returns the Holograms config data
     * @return HologramData config
     */
    HologramData getData();

}
