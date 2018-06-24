/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventorySize;
import org.bukkit.entity.Player;

class PlayerSettingsInventory extends CoreInventory {

    PlayerSettingsInventory(Player player) {
        super("", player, CoreInventorySize.ROW_6, Option.FILL_EMPTY_SLOTS);

        openInventory();
    }

}
