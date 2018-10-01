/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.inventory.anvil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface CoreAnvilInventory {

    CoreAnvilInventory setItem(AnvilSlot slot, ItemStack item);

    Inventory open(Player player);

    CoreAnvilInventory destroy();

}
