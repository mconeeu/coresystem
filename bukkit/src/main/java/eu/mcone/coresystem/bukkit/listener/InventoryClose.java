/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.anvil.AnvilInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClose implements Listener {

    @EventHandler
    public void on(InventoryCloseEvent e) {
        for (AnvilInventory inv : BukkitCoreSystem.getSystem().getPluginManager().getCoreAnvilInventories()) {
            Inventory inventory = inv.getPlayersInventory((Player) e.getPlayer());

            if (inventory != null && e.getInventory().equals(inventory)) {
                inventory.clear();
            }
        }
    }

}
