/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ProfileInventory extends CoreInventory {

    public ProfileInventory(Player p) {
        super("§8» §3§l"+p.getName()+"'s Profil", p, 36, Option.FILL_EMPTY_SLOTS);

        BukkitCoreSystem.getInstance().getMySQL(1).select("SELECT status, coins, onlinetime FROM userinfo WHERE uuid='" + player.getUniqueId().toString() + "'", rs -> {
            BukkitCorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(player);

            try {
                if (rs.next()) {
                    double onlinetime = Math.floor((cp.getOnlinetime() / 60) * 100) / 100;
                    int coins = rs.getInt("coins");
                    String status = getStatus(cp.getStatus());

                    setItem(4, ItemBuilder.createSkullItem(player.getName(), 1).displayName("§f§l" + player.getName()).lore(
                                cp.getMainGroup().getLabel(),
                                "",
                                "§7Coins: §f" + coins,
                                "§7Onlinetime: §f" + onlinetime + " Stunden",
                                "§7Status: " + status
                            ).create()
                    );

                    setItem(20, new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§f§lStats").lore("§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), () -> {
                        p.performCommand("stats");
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });

                    setItem(22, new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§9§lFreunde").lore("", "§7§oZeige deine Freunde und", "§7§oFreundschaftsanzeigen", "§7§oan!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), () -> {
                        new FriendsInventory(p);
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });

                    setItem(24, new ItemBuilder(Material.CAKE, 1, 0).displayName("§5§lParty").lore("", "§7§oZeige infos zu deiner Party", "§7§oan, oder ertselle eine!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), () -> {
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
