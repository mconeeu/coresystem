/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public class InventoryClick implements Listener{

	@EventHandler
	public void on(InventoryClickEvent e){
		if((e.getRawSlot() < e.getInventory().getSize()) && (e.getCurrentItem() != null)) {
			if (e.getCurrentItem() != null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                for (CoreInventory inv : BukkitCoreSystem.getInstance().getInventories()) {
                    if (e.getInventory().equals(inv.getInventory())) {
                        ItemStack item = e.getCurrentItem();
                        e.setCancelled(true);

                        for (HashMap.Entry<ItemStack, CoreItemEvent> entry : inv.getEvents().entrySet()) {
                            ItemStack itemStack = entry.getKey();
                            CoreItemEvent event = entry.getValue();

                            if (event != null) {
                                if (itemStack.equals(item)) {
                                    e.setCancelled(true);
                                    event.onClick(e);
                                } else if (itemStack.getType().equals(Material.SKULL_ITEM) && item.getType().equals(Material.SKULL_ITEM)) {
                                    SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                                    SkullMeta clickedMeta = (SkullMeta) item.getItemMeta();

                                    if (meta.equals(clickedMeta) || meta.hasOwner() ? (meta.getOwner().equals(clickedMeta.getOwner()) && meta.getDisplayName().equals(clickedMeta.getDisplayName())) : meta.getDisplayName().equals(clickedMeta.getDisplayName())) {
                                        event.onClick(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
		}
	}
}
