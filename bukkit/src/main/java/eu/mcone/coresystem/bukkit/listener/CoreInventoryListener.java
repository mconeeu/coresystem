/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemStack;
import eu.mcone.coresystem.api.bukkit.inventory.ItemEventStore;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEvent;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilSlot;
import eu.mcone.coresystem.api.bukkit.player.profile.PlayerInventoryProfile;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.anvil.AnvilInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public class CoreInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            Inventory inv = e.getClickedInventory();

            if (inv != null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE) && e.getRawSlot() < inv.getSize() && e.getCurrentItem() != null) {
                if (inv.getType().equals(InventoryType.PLAYER)) {
                    if (p.equals(inv.getHolder()) || p.hasPermission("system.bukkit.invsee.modify.other")) {
                        e.setCancelled(false);
                    } else {
                        e.setCancelled(true);
                        BukkitCoreSystem.getSystem().getMessager().send(p, "§4Du hast keine Berechtigung um andere Inventare zu modifizieren!");
                    }
                } else if (inv.getType().equals(InventoryType.ENDER_CHEST) || inv.getTitle().equals(PlayerInventoryProfile.ENDERCHEST_TITLE)) {
                    if (p.hasPermission("system.bukkit.ecsee.modify.other")) {
                        e.setCancelled(false);
                    } else {
                        e.setCancelled(true);
                        BukkitCoreSystem.getSystem().getMessager().send(p, "§4Du hast keine Berechtigung um andere Enderchests zu modifizieren!");
                    }
                } else if (inv.getType().equals(InventoryType.ANVIL)) {
                    for (AnvilInventory anvilInv : BukkitCoreSystem.getSystem().getPluginManager().getCoreAnvilInventories()) {
                        Inventory inventory = anvilInv.getPlayersInventory(p);

                        if (inv.equals(inventory)) {
                            e.setCancelled(true);

                            ItemStack i = e.getCurrentItem();
                            String name = "";

                            if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
                                name = i.getItemMeta().getDisplayName();
                            }

                            AnvilClickEvent event = new AnvilClickEvent(p, e, inventory, AnvilSlot.bySlot(e.getRawSlot()), name);
                            anvilInv.getHandler().onAnvilClick(event);
                            return;
                        }
                    }
                } else {
                    CoreInventory coreInv = BukkitCoreSystem.getSystem().getPluginManager().getCurrentCoreInventory(p);

                    if (coreInv != null) {
                        if (coreInv.getInventory().equals(inv)) {
                            e.setCancelled(true);
                            fireEvent(coreInv, e);
                        }
                    }
                }
            }
        }
    }

    static boolean fireEvent(ItemEventStore coreInventory, InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();

        for (HashMap.Entry<Integer, CoreItemStack> entry : coreInventory.getItems().entrySet()) {
            ItemStack itemStack = entry.getValue().getItemStack();
            CoreItemEvent event = entry.getValue().getCoreItemEvent();

            if (event != null) {
                if (itemStack.equals(item)) {
                    e.setCancelled(true);
                    event.onClick(e);
                    return true;
                } else if (itemStack.getType().equals(Material.SKULL_ITEM) && item.getType().equals(Material.SKULL_ITEM)) {
                    SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                    SkullMeta clickedMeta = (SkullMeta) item.getItemMeta();

                    if (meta.equals(clickedMeta) || meta.hasOwner() ? (meta.getOwner() != null && clickedMeta.getOwner() != null && meta.getOwner().equals(clickedMeta.getOwner()) && meta.getDisplayName().equalsIgnoreCase(clickedMeta.getDisplayName())) : meta.getDisplayName().equalsIgnoreCase(clickedMeta.getDisplayName())) {
                        event.onClick(e);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        for (AnvilInventory inv : BukkitCoreSystem.getSystem().getPluginManager().getCoreAnvilInventories()) {
            Inventory inventory = inv.getPlayersInventory((Player) e.getPlayer());

            if (inventory != null && e.getInventory().equals(inventory)) {
                inventory.clear();
            }
        }
    }

}
