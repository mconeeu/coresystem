/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StatsInventory {

    public StatsInventory(Player p) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, "§8» §3MCONE-Stats");

        for (int i = 0; i <= 26; i++) {
            inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
        }

        inv.setItem(11, ItemFactory.createItem(Material.FEATHER, 0, 1, CoreSystem.statsSkypvp.getName(), true));
        inv.setItem(13, ItemFactory.createEnchantedItem(Material.STICK, Enchantment.KNOCKBACK, 1, 0, 1, CoreSystem.statsKnockit.getName(), true));
        inv.setItem(15, ItemFactory.createItem(Material.BED, 0, 1, CoreSystem.statsBedwars.getName(), true));

        inv.setItem(18, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true));

        p.openInventory(inv);
    }

}
