/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.overwatch.report;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.core.overwatch.report.ReportReason;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ReportInventory extends CoreInventory {

    public ReportInventory(Player player, Player reported) {
        super("§8» §f§lReport", player, InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);

        int slot = InventorySlot.ROW_2_SLOT_1;
        for (Map.Entry<ReportReason, ItemStack> entry : BukkitCoreSystem.getSystem().getOverwatch().getReportManager().getReasonItems().entrySet()) {
            setItem(slot, entry.getValue(), e -> {
                if (BukkitCoreSystem.getSystem().getOverwatch().getReportManager().report(player, reported, entry.getKey())) {
                    BukkitCoreSystem.getSystem().getOverwatch().getMessenger().send(player, "§7Bitte bestätige den Report innerhalb von §a15 §7Sekunden mit §a/report confirm");
                }

                player.closeInventory();
            });

            slot++;
        }

        openInventory();
    }
}
