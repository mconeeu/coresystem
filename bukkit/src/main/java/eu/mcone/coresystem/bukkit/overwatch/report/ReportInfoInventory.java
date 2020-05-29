/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.overwatch.report;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReportInfoInventory extends CoreInventory {

    public ReportInfoInventory(Player player, Report report) {
        super("§8» §e§lReport Info", player, InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);

        try {
            OfflineCorePlayer reported = BukkitCoreSystem.getSystem().getOfflineCorePlayer(report.getReported());

            setItem(InventorySlot.ROW_1_SLOT_5, new Skull(reported.getName())
                    .setDisplayName("§e§l" + reported.getName())
                    .lore("§7Rang: §f" + reported.getMainGroup().getLabel(),
                            "§7Coins: §f" + reported.getCoins(),
                            "§7Emeralds: §f" + reported.getEmeralds(),
                            "§7Onlinezeit: §f" + reported.getOnlinetime(),
                            "§7Status: §f" + reported.getState().getName()
                    ).getItemStack());

            if (reported.getState().equals(PlayerState.ONLINE) || reported.getState().equals(PlayerState.AFK)) {
                setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.ENDER_PEARL, 1).displayName("§eNachspringen").create(), e -> {
                    //TODO: Connect the player to the SERVER
                });
            } else {
                setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.INK_SACK, 1, 7).displayName("§7Nicht verfügbar").create());
            }

            setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.IRON_FENCE, 1).displayName("§aBestrafen").lore(
                    "§7Template §8» §e" + report.getReportReason().getTemplate().getName()
            ).create(), e -> BukkitCoreSystem.getSystem().getChannelHandler().createSetRequest(player, "PUNISH", report.getReportID(), player.getUniqueId().toString()));

            setItem(InventorySlot.ROW_4_SLOT_5, new ItemBuilder(Material.DIAMOND_SWORD, 1).displayName("§7Stats").create(), e -> {
                //TODO: Add stats inventory
            });


            openInventory();
        } catch (PlayerNotResolvedException e) {
            e.printStackTrace();
        }
    }
}
