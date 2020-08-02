/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.overwatch.report;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.overwatch.Overwatch;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsInventory extends CoreInventory {

    @Getter
    private final Overwatch overwatch;
    private int currentPage = 1;

    private boolean live = true;

    private LinkedHashMap<Report, Integer> page;

    public ReportsInventory(Overwatch overwatch, Player player) {
        super("§8» §f§lReports", player, InventorySlot.ROW_6);
        this.overwatch = overwatch;
        this.page = new LinkedHashMap<>();

        setPlaceholder();
        updateItems();
        calculatePages(currentPage);
        constructContent(currentPage);
        openInventory();
    }

    private void updateItems() {
        Enchantment enchantment = Enchantment.DURABILITY;

        long open = overwatch.getReportManager().countOpenReports();
        long all = overwatch.getReportManager().countReports();
        int maxPagesOpen = (int) open / 35;
        int maxPages = (int) all / 35;

        ItemBuilder builderLiveReports = new ItemBuilder(Material.TRIPWIRE_HOOK, 1)
                .displayName((live ? "§eReports §8(§aOffen§8)" : "§eReports §8(§7Alle§8)"))
                .lore("§8➥ §7Offene Reports: " + (open > 0 ? "§c" + open : "§a" + 0),
                        "§8➥ §7Gesamt: " + (all > 0 ? "§c" + all : "§a" + 0)
                );

        if (live) {
            builderLiveReports.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_1_SLOT_1, builderLiveReports.create(), e -> {
            live = !live;
            setPlaceholder();
            calculatePages(currentPage);
            constructContent(currentPage);
            updateItems();
        });

        if (live) {
            if (currentPage <= maxPagesOpen) {
                setItem(InventorySlot.ROW_6_SLOT_9, CategoryInventory.RIGHT_ITEM, e -> constructContent(currentPage + 1));
            } else {
                setItem(InventorySlot.ROW_6_SLOT_9, CoreInventory.PLACEHOLDER_ITEM);
            }
        } else {
            if (currentPage <= maxPages) {
                setItem(InventorySlot.ROW_6_SLOT_9, CategoryInventory.RIGHT_ITEM, e -> constructContent(currentPage + 1));
            } else {
                setItem(InventorySlot.ROW_6_SLOT_9, CoreInventory.PLACEHOLDER_ITEM);
            }
        }

        if (currentPage > 1) {
            setItem(InventorySlot.ROW_6_SLOT_3, CategoryInventory.LEFT_ITEM, e -> constructContent(currentPage - 1));
        } else {
            setItem(InventorySlot.ROW_6_SLOT_3, CoreInventory.PLACEHOLDER_ITEM);
        }

        player.updateInventory();
    }

    private void calculatePages(int page) {
        this.page.clear();

        int i = 0;
        currentPage = page;

        List<Report> reports;
        if (live) {
            reports = overwatch.getReportManager().getReports(ReportState.OPEN, (currentPage == 1 ? 0 : page * 35), 35);
        } else {
            reports = overwatch.getReportManager().getReports((currentPage == 1 ? 0 : page * 35), 35);
        }

        if (!reports.isEmpty()) {
            for (Report report : reports) {
                this.page.put(report, report.getPriority().getLevel());

                if (i <= 34) {
                    ++i;
                }
            }
        } else {
            setItem(InventorySlot.ROW_3_SLOT_5, new ItemBuilder(Material.BARRIER, 1).displayName("§cKeine Reports gefunden").create());
        }

        this.page = this.page.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private void constructContent(int page) {
        currentPage = page;
        int row = 0;
        int currentRow;

        int slot = InventorySlot.ROW_1_SLOT_3;
        if (this.page.size() > 0) {
            for (Report report : this.page.keySet()) {
                if (slot <= 39) {
                    if (report.getState().equals(ReportState.OPEN)) {
                        setItem(slot, getItem(report), e -> {
                            BukkitCoreSystem.getSystem().getChannelHandler().createSetRequest(player, "REPORT", "ACCEPT", report.getID(), player.getUniqueId().toString());
                            player.closeInventory();
                        });
                    } else {
                        setItem(slot, getItem(report), e -> {

                            player.closeInventory();
                        });
                    }

                    currentRow = (slot - 2) / 6;
                    slot++;

                    if (currentRow > row) {
                        slot += 2;
                        row++;
                    }
                }
            }
        } else {
            setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.BARRIER, 1).displayName("§cKeine Reports verfügbar").create());
        }

        player.updateInventory();
    }

    private void setPlaceholder() {
        setItem(InventorySlot.ROW_1_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_1_SLOT_3, null);
        setItem(InventorySlot.ROW_1_SLOT_4, null);
        setItem(InventorySlot.ROW_1_SLOT_5, null);
        setItem(InventorySlot.ROW_1_SLOT_6, null);
        setItem(InventorySlot.ROW_1_SLOT_7, null);
        setItem(InventorySlot.ROW_1_SLOT_8, null);
        setItem(InventorySlot.ROW_1_SLOT_9, null);

        setItem(InventorySlot.ROW_2_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_2_SLOT_3, null);
        setItem(InventorySlot.ROW_2_SLOT_4, null);
        setItem(InventorySlot.ROW_2_SLOT_5, null);
        setItem(InventorySlot.ROW_2_SLOT_6, null);
        setItem(InventorySlot.ROW_2_SLOT_7, null);
        setItem(InventorySlot.ROW_2_SLOT_8, null);
        setItem(InventorySlot.ROW_2_SLOT_9, null);

        setItem(InventorySlot.ROW_3_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_3_SLOT_3, null);
        setItem(InventorySlot.ROW_3_SLOT_4, null);
        setItem(InventorySlot.ROW_3_SLOT_5, null);
        setItem(InventorySlot.ROW_3_SLOT_6, null);
        setItem(InventorySlot.ROW_3_SLOT_7, null);
        setItem(InventorySlot.ROW_3_SLOT_8, null);
        setItem(InventorySlot.ROW_3_SLOT_9, null);

        setItem(InventorySlot.ROW_4_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_4_SLOT_3, null);
        setItem(InventorySlot.ROW_4_SLOT_4, null);
        setItem(InventorySlot.ROW_4_SLOT_5, null);
        setItem(InventorySlot.ROW_4_SLOT_6, null);
        setItem(InventorySlot.ROW_4_SLOT_7, null);
        setItem(InventorySlot.ROW_4_SLOT_8, null);
        setItem(InventorySlot.ROW_4_SLOT_9, null);

        setItem(InventorySlot.ROW_5_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_3, null);
        setItem(InventorySlot.ROW_5_SLOT_4, null);
        setItem(InventorySlot.ROW_5_SLOT_5, null);
        setItem(InventorySlot.ROW_5_SLOT_6, null);
        setItem(InventorySlot.ROW_5_SLOT_7, null);
        setItem(InventorySlot.ROW_5_SLOT_8, null);
        setItem(InventorySlot.ROW_5_SLOT_9, null);

        setItem(InventorySlot.ROW_6_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_3, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_4, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_5, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_6, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_7, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_8, CoreInventory.PLACEHOLDER_ITEM);
    }

    private ItemStack getItem(Report report) {
        try {
            OfflineCorePlayer reporter = BukkitCoreSystem.getSystem().getOfflineCorePlayer(report.getReporter().get(0));
            OfflineCorePlayer reported = BukkitCoreSystem.getSystem().getOfflineCorePlayer(report.getReported());

            Material material = Material.PAPER;
            switch (report.getPriority()) {
                case MEDIUM:
                    material = Material.MAP;
                    break;
                case HIGH:
                    material = Material.BOOK;
                    break;
                case EXTREME:
                    material = Material.BOOK_AND_QUILL;
                    break;
            }

            return new ItemBuilder(material, 1)
                    .displayName("§l" + report.getPriority().getLabel() + reported.getName())
                    .lore(
                            "§7Reportet um: §e" + new SimpleDateFormat("HH:mm").format(new Date(report.getTimestamp() * 1000)),
                            "§7Grund: §e" + report.getReason().getName(),
                            "§7Priorität: " + report.getPriority().getPrefix(),
                            "§7Reporteter Spieler: §e" + reporter.getName(),
                            "",
                            (report.getState().equals(ReportState.OPEN) ? "§8» §7Linksklick §8| §7§oAnnehmen" : "§8» §7Linksklick §8| §7§oInfos")
                    ).create();
        } catch (PlayerNotResolvedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
