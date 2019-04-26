/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import org.bukkit.entity.Player;

public interface ProfileInventoryModifier {

    /**
     * Use this to modify the ProfileInventory (opens with /profile)
     * You can adjust the size and add items with ClickEvents
     * @param inventory the inventory
     * @param player the player for which it will open
     */
    void onCreate(CoreInventory inventory, Player player);

}
