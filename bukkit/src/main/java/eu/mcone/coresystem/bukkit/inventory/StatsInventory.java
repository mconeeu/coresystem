/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StatsInventory extends CoreInventory {

    public StatsInventory(Player p) {
        super("§8» §3MCONE-Stats", p, 27, Option.FILL_EMPTY_SLOTS);

        setItem(11, ItemFactory.createItem(Material.FEATHER, 0, 1, CoreSystem.statsSkypvp.getName(), true), () -> {
            new StatsCategoryInventory(p, CoreSystem.statsSkypvp);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });
        setItem(13, ItemFactory.createEnchantedItem(Material.STICK, Enchantment.KNOCKBACK, 1, 0, 1, CoreSystem.statsKnockit.getName(), true), () -> {
            new StatsCategoryInventory(p, CoreSystem.statsKnockit);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });
        setItem(15, ItemFactory.createItem(Material.BED, 0, 1, CoreSystem.statsBedwars.getName(), true), () -> {
            new StatsCategoryInventory(p, CoreSystem.statsBedwars);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(18, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true), () -> {
            new ProfileInventory(p);
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
        });

        openInventory();
    }

}
