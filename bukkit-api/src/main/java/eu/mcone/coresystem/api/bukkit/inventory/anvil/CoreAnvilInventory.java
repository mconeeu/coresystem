/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.anvil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface CoreAnvilInventory {

    /**
     * sets an item in the inventory
     *
     * @param slot anvil slot
     * @param item item stack
     * @return this
     */
    CoreAnvilInventory setItem(AnvilSlot slot, ItemStack item);

    /**
     * opens the anvil inventory for a player
     *
     * @param player target player
     * @return returns the opened inventory
     */
    Inventory open(Player player);

    /**
     * destroys the Inventory with the AnvilClickEventHandler
     *
     * @return returns the destroyed anvil inventory
     */
    CoreAnvilInventory destroy();

}
