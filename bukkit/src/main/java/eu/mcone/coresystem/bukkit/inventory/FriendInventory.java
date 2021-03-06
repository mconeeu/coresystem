/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

class FriendInventory extends CoreInventory {

    FriendInventory(Player p, String friend) {
        super("§8» §f§l" + friend + " §8| §7Aktionen", p, InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);

        setItem(InventorySlot.ROW_1_SLOT_5, new Skull(friend, 1).toItemBuilder().displayName("§f§l" + friend).create());
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
            Sound.error(p);
            new FriendsInventory(p);
        });

        openInventory();
    }

}
