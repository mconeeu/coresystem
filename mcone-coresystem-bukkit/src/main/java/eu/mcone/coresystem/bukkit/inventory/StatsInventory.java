/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class StatsInventory extends CoreInventory {

    public StatsInventory(Player p) {
        super("§8» §3MCONE-Stats", p, 27, Option.FILL_EMPTY_SLOTS);

        setItem(11, new ItemBuilder(Material.FEATHER, 1, 0).displayName(Gamemode.SKYPVP.getLabel()).create(), () -> {
            new StatsCategoryInventory(p, BukkitCoreSystem.getInstance().getStatsAPI(Gamemode.SKYPVP));
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });
        setItem(13, new ItemBuilder(Material.STICK, 1, 0).enchantment(Enchantment.KNOCKBACK, 1).displayName(Gamemode.KNOCKIT.getLabel()).create(), () -> {
            new StatsCategoryInventory(p, BukkitCoreSystem.getInstance().getStatsAPI(Gamemode.KNOCKIT));
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });
        setItem(15, new ItemBuilder(Material.BED, 1, 0).displayName(Gamemode.BEDWARS.getLabel()).create(), () -> {
            new StatsCategoryInventory(p, BukkitCoreSystem.getInstance().getStatsAPI(Gamemode.BEDWARS));
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(18, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), () -> {
            new ProfileInventory(p);
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
        });

        openInventory();
    }

}
