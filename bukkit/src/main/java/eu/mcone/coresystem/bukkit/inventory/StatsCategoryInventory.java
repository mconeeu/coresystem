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
import eu.mcone.coresystem.api.bukkit.player.Stats;
import org.bukkit.Material;
import org.bukkit.entity.Player;

class StatsCategoryInventory extends CoreInventory {

    StatsCategoryInventory(Player p, Stats stats) {
        super("§8» §3MCONE-Stats", p, InventorySlot.ROW_3, InventoryOption.FILL_EMPTY_SLOTS);
        int[] result = stats.getData();

        setItem(InventorySlot.ROW_2_SLOT_5, new ItemBuilder(Material.ITEM_FRAME, 1, 0).displayName("§3§l" + stats.getGamemode().getLabel() + " §8| §7Global Stats").lore(
                "§7Platz: §f" + result[0],
                "§7Kills: §f" + result[1],
                "§7Tode: §f" + result[2]
                ).create()
        );

        setItem(InventorySlot.ROW_3_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Stats Menü").create(), e -> {
            new StatsInventory(p);
            Sound.error(p);
        });

        openInventory();
    }

}
