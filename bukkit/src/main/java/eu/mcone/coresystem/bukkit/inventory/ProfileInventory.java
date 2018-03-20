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

public class ProfileInventory extends CoreInventory {

    public ProfileInventory(Player p) {
        super("§8» §8Profil von §3§l"+p.getName(), p, 36, Option.FILL_EMPTY_SLOTS);

        CoreSystem.mysql1.select("SELECT status, coins, onlinetime FROM userinfo WHERE uuid='" + player.getUniqueId().toString() + "'", rs -> {
            CorePlayer cp = CoreSystem.getCorePlayer(player);

            try {
                if (rs.next()) {
                    double onlinetime = Math.floor((cp.getOnlinetime() / 60) * 100) / 100;
                    int coins = rs.getInt("coins");
                    String status = getStatus(cp.getStatus());

                    setItem(4, ItemFactory.createSkullItem("§f§l" + player.getName(), player.getName(), 1, new ArrayList<>(Arrays.asList(cp.getGroup().getLabel(), "", "§7Coins: §f" + coins, "§7Onlinetime: §f" + onlinetime + " Stunden", "§7Status: " + status))));

                    setItem(20, ItemFactory.createItem(Material.NETHER_STAR, 0, 1, "§f§lStats", new ArrayList<>(Arrays.asList("", "§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!")), true), () -> {
                        p.performCommand("stats");
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });

                    setItem(22, ItemFactory.createItem(Material.SKULL_ITEM, 3, 1, "§9§lFreunde", new ArrayList<>(Arrays.asList("", "§7§oZeige deine Freunde und", "§7§oFreundschaftsanzeigen", "§7§oan!")), true), () -> {
                        new FriendsInventory(p);
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });

                    setItem(24, ItemFactory.createItem(Material.CAKE, 0, 1, "§5§lParty", new ArrayList<>(Arrays.asList("", "§7§oZeige infos zu deiner Party", "§7§oan, oder ertselle eine!")), true), () -> {
                        new PartyInventory(p);
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });

                    openInventory();
                } else {
                    System.err.println("Player "+player+" is not registred in the database");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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
