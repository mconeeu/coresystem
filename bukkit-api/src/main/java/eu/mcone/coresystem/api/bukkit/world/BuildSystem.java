/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import org.bukkit.World;
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
     * default ListMode is Blacklist with no worlds, which means BuildSystem rules are applied to all worlds
     * if you want the BuildSystem rules to only work at specific worlds blacklist the used worlds or use the whitelist to apply rules only to used worlds
     * notice that new loaded worlds will not get added to black or whitelist automatically!
     * @param mode list mode
     * @param worlds worlds that stay on the list
     */
    void setWorlds(ListMode mode, World... worlds);

    /**
     * should players be informed if their action is blocked by the BuildSystem?
     * @param notify inform players on blocked action
     */
    void setNotify(boolean notify);

    /**
     * should players with build permissions like system.bukkit.build.<world>.<event>.<blockid> should bypass build restrictions?
     * @param useBuildPermissionNodes if players with build permissions should bypass build restrictions
     */
    void setUseBuildPermissionNodes(boolean useBuildPermissionNodes);

    /**
     * filter specific items for the given event
     * this items dont get blocked by given event from the BuildSystem
     * @param event event
     * @param filter array of material ids that should be filtered
     */
    void addFilter(BuildEvent event, Integer... filter);

}
