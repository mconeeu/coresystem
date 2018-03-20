/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.inventory.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
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
                for (CoreInventory inv : CoreSystem.getInstance().getInventories()) {
                    if (e.getInventory().equals(inv.getInventory())) {
                        ItemStack item = e.getCurrentItem();
                        e.setCancelled(true);

                        for (HashMap.Entry<ItemStack, CoreItemEvent> entry : inv.getEvents().entrySet()) {
                            ItemStack itemStack = entry.getKey();
                            CoreItemEvent event = entry.getValue();

                            if (itemStack.equals(item)) {
                                if (event != null) {
                                    e.setCancelled(true);
                                    event.onClick();
                                }
                            } else if (itemStack.getType().equals(Material.SKULL_ITEM) && item.getType().equals(Material.SKULL_ITEM)) {
                                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                                SkullMeta clickedMeta = (SkullMeta) item.getItemMeta();

                                if (meta.getOwner().equals(clickedMeta.getOwner())) {
                                    event.onClick();
                                }
                            }
                        }
                    }
                }
            }
		}
	}
}
