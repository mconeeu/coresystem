/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.player.StatsAPI;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class StatsCategoryInventory extends CoreInventory {

    StatsCategoryInventory(Player p, StatsAPI stats) {
        super("§8» §3MCONE-Stats", p, 27, Option.FILL_EMPTY_SLOTS);

        int[] result = stats.getData(player.getUniqueId());

        setItem(13, new ItemBuilder(Material.ITEM_FRAME, 1, 0).displayName("§3§l" + stats.getGamemode().getLabel() + " §8| §7Global Stats").lore(
                        "§7Platz: §f" + result[0],
                        "§7Kills: §f" + result[1],
                        "§7Tode: §f" + result[2]
                ).create()
        );

        setItem(18, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Stats Menü").create(), () -> {
            new StatsInventory(p);
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
        });

        openInventory();
    }

}
