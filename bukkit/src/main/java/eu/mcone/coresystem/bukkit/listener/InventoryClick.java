/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEvent;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilSlot;
import eu.mcone.coresystem.bukkit.inventory.anvil.AnvilInventory;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public class InventoryClick implements Listener{

	@EventHandler
	public void on(InventoryClickEvent e){
	    if (e.getWhoClicked() instanceof Player) {
            if ((e.getRawSlot() < e.getInventory().getSize()) && (e.getCurrentItem() != null)) {
                if (e.getCurrentItem() != null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                    ItemStack item = e.getCurrentItem();

                    for (CoreInventory inv : BukkitCoreSystem.getSystem().getPluginManager().getCoreInventories()) {
                        if (e.getInventory().equals(inv.getInventory())) {
                            e.setCancelled(true);

                            for (HashMap.Entry<ItemStack, CoreItemEvent> entry : inv.getEvents().entrySet()) {
                                ItemStack itemStack = entry.getKey();
                                CoreItemEvent event = entry.getValue();

                                if (event != null) {
                                    if (itemStack.equals(item)) {
                                        e.setCancelled(true);
                                        event.onClick(e);
                                        return;
                                    } else if (itemStack.getType().equals(Material.SKULL_ITEM) && item.getType().equals(Material.SKULL_ITEM)) {
                                        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                                        SkullMeta clickedMeta = (SkullMeta) item.getItemMeta();

                                        if (meta.equals(clickedMeta) || meta.hasOwner() ? (meta.getOwner().equals(clickedMeta.getOwner()) && meta.getDisplayName().equals(clickedMeta.getDisplayName())) : meta.getDisplayName().equals(clickedMeta.getDisplayName())) {
                                            event.onClick(e);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (AnvilInventory inv : BukkitCoreSystem.getSystem().getPluginManager().getCoreAnvilInventories()) {
                        Player p = (Player) e.getWhoClicked();
                        Inventory inventory = inv.getPlayersInventory(p);

                        if (inventory != null && e.getInventory().equals(inventory)) {
                            e.setCancelled(true);

                            ItemStack i = e.getCurrentItem();
                            String name = "";

                            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                                name = i.getItemMeta().getDisplayName();
                            }

                            AnvilClickEvent event = new AnvilClickEvent(p, e, inventory, AnvilSlot.bySlot(e.getRawSlot()), name);
                            inv.getHandler().onAnvilClick(event);
                            return;
                        }
                    }
                }
            }
        }
	}
}
