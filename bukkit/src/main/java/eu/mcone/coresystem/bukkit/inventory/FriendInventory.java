/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class FriendInventory extends CoreInventory {

    FriendInventory(Player p, String friend) {
        super("§8» §f§l" + friend + " §8| §7Aktionen", p, InventorySlot.ROW_4, Option.FILL_EMPTY_SLOTS);

        setItem(InventorySlot.ROW_1_SLOT_5, ItemBuilder.createSkullItem(friend, 1).displayName("§f§l" + friend).create());
        setItem(InventorySlot.ROW_3_SLOT_3, new ItemBuilder(Material.ENDER_PEARL, 1, 0).displayName("§7Teleportieren").create(), e -> {
            p.closeInventory();
            BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "jump " + friend);
        });
        setItem(InventorySlot.ROW_3_SLOT_5, new ItemBuilder(Material.CAKE, 1, 0).displayName("§7In §5Party §7einladen").create(), e -> {
            p.closeInventory();
            BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party invite " + friend);
        });
        setItem(InventorySlot.ROW_3_SLOT_7, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§4Freund entfernen").create(), e -> {
            p.closeInventory();
            BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "friend remove " + friend);
        });
        setItem(InventorySlot.ROW_4_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Freundemenü").create(), e -> {
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new FriendsInventory(p);
        });

        openInventory();
    }

}
