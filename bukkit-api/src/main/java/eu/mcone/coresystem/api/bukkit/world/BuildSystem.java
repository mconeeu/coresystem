/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface BuildSystem {

    enum BuildEvent {
        BLOCK_BREAK, BLOCK_PLACE, INTERACT
    }

    /**
     * changes a players build mode
     * @param player player
     */
    void changeBuildMode(Player player);

    /**
     * checks if player has build mode enabled
     * @param player player
     * @return boolean enabled
     */
    boolean hasBuildModeEnabled(Player player);

    /**
     * should players be informed if their action is blocked by the BuildSystem?
     * @param notify inform players on blocked action
     */
    void setNotifying(boolean notify);

    /**
     * filter specific items for the given event
     * this items dont get blocked by given event from the BuildSystem
     * @param event event
     * @param filter array of materials that should be filtered
     */
    void addFilter(BuildEvent event, Material... filter);

}
