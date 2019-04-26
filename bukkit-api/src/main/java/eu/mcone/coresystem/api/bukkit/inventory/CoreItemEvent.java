/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface CoreItemEvent {

    /**
     * gets called when the player clicks on an item in the inventory
     * @param e InventoryClickEvent
     */
    void onClick(InventoryClickEvent e);

}
