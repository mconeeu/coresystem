/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import org.bukkit.entity.Player;

public interface BuildSystem {

    enum BuildEvent {
        BLOCK_BREAK, BLOCK_PLACE, INTERACT
    }

    void changeBuildMode(Player player);

    boolean hasBuildModeEnabled(Player player);

}
