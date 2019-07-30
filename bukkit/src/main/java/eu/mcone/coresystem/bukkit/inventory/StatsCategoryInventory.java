/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.player.Stats;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class StatsCategoryInventory extends CoreInventory {

    StatsCategoryInventory(Player p, Stats stats) {
        super("§8» §3MCONE-Stats", p, InventorySlot.ROW_3, InventoryOption.FILL_EMPTY_SLOTS);
        int[] result = stats.getData();

        setItem(InventorySlot.ROW_2_SLOT_5, new ItemBuilder(Material.ITEM_FRAME).displayName("§3§l" + stats.getGamemode().getLabel() + " §8| §7Global Stats").lore(
                "§7Platz: §f" + result[0],
                "§7Kills: §f" + result[1],
                "§7Tode: §f" + result[2]
                ).create()
        );

        setItem(InventorySlot.ROW_3_SLOT_1, new ItemBuilder(Material.IRON_DOOR).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
            new ProfileInventory(p).openInventory();
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        });
    }

}
