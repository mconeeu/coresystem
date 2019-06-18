/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEvent;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilSlot;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifiedInventory;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CoreInventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem() != null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                    if (e.getClickedInventory().getType().equals(InventoryType.ENDER_CHEST)) {
                        if (e.getClickedInventory().getHolder() instanceof Player && !e.getClickedInventory().getHolder().equals(e.getWhoClicked())) {
                            switch (e.getClickedInventory().getType()) {
                                case PLAYER: {
                                    if (e.getWhoClicked().hasPermission("system.bukkit.invsee.modify.other")) {
                                        e.setCancelled(false);
                                    } else {
                                        e.setCancelled(true);
                                        BukkitCoreSystem.getSystem().getMessager().send(e.getWhoClicked(), "§4Du hast keine Berechtigung um andere Inventare zu modifizieren!");
                                    }
                                    break;
                                }
                                case ENDER_CHEST: {
                                    if (e.getWhoClicked().hasPermission("system.bukkit.ecsee.modify.other")) {
                                        e.setCancelled(false);
                                    } else {
                                        e.setCancelled(true);
                                        BukkitCoreSystem.getSystem().getMessager().send(e.getWhoClicked(), "§4Du hast keine Berechtigung um andere Enderchests zu modifizieren!");
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        for (ModifyInventory modifyInventory : CoreSystem.getInstance().getInventoryModificationManager().getInventories()) {
                            if (e.getInventory().getTitle().equalsIgnoreCase(modifyInventory.getTitle())) {
                                if (CoreSystem.getInstance().getInventoryModificationManager().isCurrentlyModifying(player)
                                        && CoreSystem.getInstance().getInventoryModificationManager().getCurrentlyModifying(player).getTitle().equalsIgnoreCase(e.getInventory().getName())) {

                                    //Cancel click for PlayerInventory
                                    if (e.getRawSlot() > e.getInventory().getSize()) {
                                        e.setCancelled(true);
                                    } else {
                                        e.setCancelled(false);
                                    }
                                } else {
                                    e.setCancelled(!modifyInventory.getOptions().contains(CoreInventory.Option.ENABLE_CLICK_EVENT));
                                    fireEvent(modifyInventory, e);
                                }
                            }
                        }

                        for (CoreInventory inv : BukkitCoreSystem.getSystem().getPluginManager().getCoreInventories()) {
                            if (e.getInventory().getTitle().equalsIgnoreCase(inv.getTitle())) {
                                if (e.getRawSlot() > e.getInventory().getSize()) {
                                    e.setCancelled(true);
                                } else {
                                    e.setCancelled(!inv.getOptions().contains(CoreInventory.Option.ENABLE_CLICK_EVENT));
                                }

                                fireEvent(inv, e);
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

    private boolean fireEvent(CoreInventory coreInventory, InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();

        for (HashMap.Entry<Integer, CoreInventory.CoreItemStack> entry : coreInventory.getItems().entrySet()) {
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

                    if (meta.equals(clickedMeta) || meta.hasOwner() ? (meta.getOwner().equals(clickedMeta.getOwner()) && meta.getDisplayName().equals(clickedMeta.getDisplayName())) : meta.getDisplayName().equals(clickedMeta.getDisplayName())) {
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
        Player player = (Player) e.getPlayer();

        //Check if the player edits currently a inventory
        if (CoreSystem.getInstance().getInventoryModificationManager().isCurrentlyModifying(player)) {
            for (ModifyInventory modifyInventory : CoreSystem.getInstance().getInventoryModificationManager().getInventories()) {
                if (modifyInventory.getTitle().equalsIgnoreCase(e.getInventory().getName())) {
                    if (CoreSystem.getInstance().getInventoryModificationManager().getCurrentlyModifying(player).getTitle().equalsIgnoreCase(e.getInventory().getName())) {
                        CoreSystem.getInstance().getInventoryModificationManager().modifyInventory(player, modifyInventory, e.getInventory());
                    }
                }
            }
        }

        for (AnvilInventory inv : BukkitCoreSystem.getSystem().getPluginManager().getCoreAnvilInventories()) {
            Inventory inventory = inv.getPlayersInventory((Player) e.getPlayer());

            if (inventory != null && e.getInventory().equals(inventory)) {
                inventory.clear();
            }
        }
    }

}
