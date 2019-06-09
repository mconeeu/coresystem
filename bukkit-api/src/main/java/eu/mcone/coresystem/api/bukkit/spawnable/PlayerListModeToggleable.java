/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.spawnable;

import org.bukkit.entity.Player;

import java.util.Set;

public interface PlayerListModeToggleable {

    ListMode getListMode();

    Set<Player> getVisiblePlayersList();

    void togglePlayerVisibility(ListMode listMode, Player... players);

    /**
     * makes the NPC either visible or unvisible for a specific player
     * this will add|remove the player to the NPCs black-|whitelist
     * @param player target player
     * @param canSee if the NPC should be shown or hidden
     */
    void toggleVisibility(Player player, boolean canSee);

    /**
     * returns if the player is allowed to see the NPC if the player is in its range. Calculated by the NpcVisibility settings
     * @param player target player
     * @return if player is allowed to see NPC due to NpcVisibility settings
     */
    boolean isVisibleFor(Player player);

}
