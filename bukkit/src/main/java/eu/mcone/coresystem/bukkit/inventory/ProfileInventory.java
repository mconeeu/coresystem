/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ProfileInventory extends CoreInventory {

    public ProfileInventory(Player p) {
        super("§8» §3§l" + p.getName() + "'s Profil", p, InventorySlot.ROW_4, Option.FILL_EMPTY_SLOTS);

        CorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(p);
        double onlinetime = Math.floor(((double) cp.getOnlinetime() / 60) * 100) / 100;
        String status = cp.getState().getName();

        setItem(InventorySlot.ROW_1_SLOT_5, ItemBuilder.createSkullItem(p.getName(), 1).displayName("§f§l" + p.getName()).lore(
                cp.getMainGroup().getLabel(),
                "",
                "§7Coins: §f" + cp.getCoins(),
                "§7Onlinetime: §f" + onlinetime + " Stunden",
                "§7Status: " + status
                ).create()
        );

        setItem(InventorySlot.ROW_3_SLOT_2, new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§f§lStats").lore("§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            p.performCommand("stats");
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§9§lFreunde").lore("§7§oZeige deine Freunde und", "§7§oFreundschaftsanzeigen", "§7§oan!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new FriendsInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.CAKE, 1, 0).displayName("§5§lParty").lore("§7§oZeige infos zu deiner Party", "§7§oan, oder ertselle eine!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new PartyInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_8, new ItemBuilder(Material.REDSTONE, 1, 0).displayName("§c§lEinstellungen").lore("§7§oVerwalte dein Konto und andere", "§7§oingame Einstellungen", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new PlayerSettingsInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        openInventory();
    }

}
