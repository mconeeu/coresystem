/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.modify;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import org.bukkit.entity.Player;

public interface CoreInventoryModifier {

    /**
     * Use this to modify the CoreInventories title, size or flags
     * This can not be changed later.
     *
     * @param entry Initialize entry
     */
    void onInitialize(CoreInventoryInitializeEntry entry);

    /**
     * Use this to modify any CoreInventory
     * You can add items with ClickEvents
     *
     * @param inventory the inventory
     * @param player    the player for which it will open
     */
    void onCreate(CoreInventory inventory, Player player);

}
