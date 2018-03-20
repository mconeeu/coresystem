/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.api.StatsAPI;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Arrays;

class StatsCategoryInventory extends CoreInventory {

    StatsCategoryInventory(Player p, StatsAPI stats) {
        super("§8» §3MCONE-Stats", p, 27, Option.FILL_EMPTY_SLOTS);

        int[] result = stats.getData(player);

        setItem(13, ItemFactory.createItem(Material.ITEM_FRAME, 0, 1, "§3§l" + stats.getName() + " §8| §7Global Stats",
                new ArrayList<>(Arrays.asList("§7Platz: §f" + result[0],
                        "§7Kills: §f" + result[1],
                        "§7Tode: §f" + result[2]
                )),
                true
        ));

        setItem(18, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Stats Menü", true), () -> {
            new StatsInventory(p);
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
        });

        openInventory();
    }

}
