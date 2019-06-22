/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class FriendsInventory extends CoreInventory {

    FriendsInventory(Player p) {
        super("§8» §9§lMeine Freunde", p, InventorySlot.ROW_6, InventoryOption.FILL_EMPTY_SLOTS);

        BukkitCoreSystem.getInstance().getChannelHandler().createGetRequest(p, friends -> {
            int i = 0;
            for (String friend : friends.split(",")) {
                if (friend.equals("") || i > 44) continue;

                String[] data = friend.split(":");
                setItem(i, new Skull(data[1], 1).toItemBuilder().displayName("§f§l" + data[1]).lore(data[2], "", "§8» §f§nRechtsklick§8 | §7§oAktionen").create(), e -> {
                    new FriendInventory(p, data[1]).openInventory();
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                });

                i++;
            }

            setItem(InventorySlot.ROW_6_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                new ProfileInventory(p).openInventory();
            });
        }, "FRIENDS");
    }

}
