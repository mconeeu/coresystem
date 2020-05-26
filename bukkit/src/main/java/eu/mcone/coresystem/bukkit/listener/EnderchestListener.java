/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.EnderchestManagerGetter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class EnderchestListener implements Listener {

    private EnderchestManagerGetter apiGetter;

    public EnderchestListener(EnderchestManagerGetter apiGetter) {
        this.apiGetter = apiGetter;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(InventoryOpenEvent e) {
        if (e.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            Player p = (Player) e.getPlayer();

            e.setCancelled(true);
            p.openInventory(apiGetter.getEnderchestManager(p).getEnderchest());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equals("§8Deine Enderkiste")) {
            apiGetter.getEnderchestManager((Player) e.getPlayer()).updateEnderchest(e.getInventory());
        }
    }

    @EventHandler
    public void onPermissionChange(PermissionChangeEvent e) {
        apiGetter.getEnderchestManager(e.getPlayer().bukkit()).reload();
    }

}
