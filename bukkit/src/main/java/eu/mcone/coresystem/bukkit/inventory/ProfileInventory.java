/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileInventory {

    public ProfileInventory(CorePlayer p) {
        CoreSystem.mysql1.select("SELECT status, coins, onlinetime FROM userinfo WHERE uuid='" + p.getUuid().toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    double onlinetime = Math.floor((p.getOnlinetime() / 60) * 100) / 100;
                    int coins = rs.getInt("coins");
                    String status = getStatus(p.getStatus());

                    Inventory inv = org.bukkit.Bukkit.createInventory(null, 36, "§8» §8Profil von §3§l"+p.getName());

                    for (int i = 0; i <= 35; i++) {
                        inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
                    }
                    inv.setItem(4, ItemFactory.createSkullItem("§f§l" + p.getName(), p.getName(), 1, new ArrayList<>(Arrays.asList(p.getGroup().getLabel(), "","§7Coins: §f" + coins , "§7Onlinetime: §f" + onlinetime + " Stunden", "§7Status: " + status))));

                    inv.setItem(20, ItemFactory.createItem(Material.NETHER_STAR, 0, 1, "§f§lStats", new ArrayList<>(Arrays.asList("", "§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!")), true));
                    inv.setItem(22, ItemFactory.createItem(Material.SKULL_ITEM, 3, 1, "§9§lFreunde", new ArrayList<>(Arrays.asList("", "§7§oZeige deine Freunde und", "§7§oFreundschaftsanzeigen", "§7§oan!")), true));
                    inv.setItem(24, ItemFactory.createItem(Material.CAKE, 0, 1, "§5§lParty", new ArrayList<>(Arrays.asList("", "§7§oZeige infos zu deiner Party", "§7§oan, oder ertselle eine!")), true));

                    p.bukkit().openInventory(inv);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void click(InventoryClickEvent e, Player p) {
        if ((e.getCurrentItem() == null) || !e.getCurrentItem().hasItemMeta() || e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§f§lStats")){
            p.performCommand("stats");
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§9§lFreunde")){
            new FriendsInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5§lParty")){
            new PartyInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        }
    }

    private static String getStatus(String status) {
        switch (status) {
            case "online":
                return "§aonline";
            case "afk":
                return "§6AFK";
            default:
                return "§aonline";
        }
    }
}
