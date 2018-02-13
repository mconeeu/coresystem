/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.api.StatsAPI;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;

public class StatsCategoryInventory {

    public StatsCategoryInventory(Player p, StatsAPI stats) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, "§8» §3MCONE-Stats");

        for (int i = 0; i <= 26; i++) {
            inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
        }

        int[] result = stats.getData(p);

        inv.setItem(13, ItemFactory.createItem(Material.ITEM_FRAME, 0, 1, "§3§l" + stats.getName() + " §8| §7Global Stats",
                new ArrayList<>(Arrays.asList("§7Platz: §f" + result[0],
                        "§7Kills: §f" + result[1],
                        "§7Tode: §f" + result[2]
                )),
                true
        ));
        inv.setItem(18, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Stats Menü", true));

        p.openInventory(inv);
    }

}
