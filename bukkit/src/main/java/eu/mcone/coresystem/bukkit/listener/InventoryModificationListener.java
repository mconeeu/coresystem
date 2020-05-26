/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.modification.CoreInventoryModificationManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class InventoryModificationListener implements Listener {

    private final CoreInventoryModificationManager api;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        api.loadModifiedInventories(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            Inventory inv = e.getClickedInventory();

            if (e.getCurrentItem() != null && e.getRawSlot() < inv.getSize()) {
                if (e.getCurrentItem() != null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                    for (ModifyInventory modifiedInv : api.getModifyInventories()) {
                        Inventory currentInv = modifiedInv.getCurrentInventory(p);

                        if (currentInv != null && currentInv.equals(inv)) {
                            if (api.isCurrentlyModifying(p) && api.getCurrentlyModifyingInventory(p).equals(modifiedInv)) {
                                e.setCancelled(false);
                            } else {
                                e.setCancelled(true);
                                CoreInventoryListener.fireEvent(modifiedInv, e);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();

            Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getSystem(), () -> {
                //Check if the player edits currently a inventory
                if (api.isCurrentlyModifying(p)) {
                    ModifyInventory currentlyModifying = api.getCurrentlyModifyingInventory(p);
                    Inventory playerInv = currentlyModifying.getCurrentInventory(p);

                    if (playerInv != null && e.getInventory().equals(playerInv)) {
                        api.openCategoryModificationInventory(p, currentlyModifying.getGamemode(), currentlyModifying.getCategory());
                        api.modifyInventory(p, currentlyModifying, e.getInventory());
                        api.removeCurrentlyModifying(p);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        api.saveModifications(e.getPlayer().getUniqueId());
        api.unloadPlayer(e.getPlayer());
    }

}
