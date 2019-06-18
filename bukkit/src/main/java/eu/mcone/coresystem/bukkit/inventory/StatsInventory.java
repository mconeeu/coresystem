/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class StatsInventory extends CoreInventory {

    public StatsInventory() {
        super("§8» §f§lStatistiken", InventorySlot.ROW_3, Option.FILL_EMPTY_SLOTS);
    }

    public void createInventory(Player p) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        setItem(InventorySlot.ROW_2_SLOT_3, new ItemBuilder(Material.FEATHER, 1, 0).displayName(Gamemode.SKYPVP.getLabel()).create(), e -> {
            new StatsCategoryInventory().createInventory(p, cp.getStats(Gamemode.SKYPVP));
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });
        setItem(InventorySlot.ROW_2_SLOT_5, new ItemBuilder(Material.STICK, 1, 0).enchantment(Enchantment.KNOCKBACK, 1).displayName(Gamemode.KNOCKIT.getLabel()).itemFlags(ItemFlag.HIDE_ENCHANTS).create(), e -> {
            new StatsCategoryInventory().createInventory(p, cp.getStats(Gamemode.KNOCKIT));
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });
        setItem(InventorySlot.ROW_2_SLOT_7, new ItemBuilder(Material.BED, 1, 0).displayName(Gamemode.BEDWARS.getLabel()).create(), e -> {
            new StatsCategoryInventory().createInventory(p, cp.getStats(Gamemode.BEDWARS));
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
            new ProfileInventory().createInventory(p);
            new ProfileInventory().openInventory(p);
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
        });

        super.openInventory(p);
    }
}
