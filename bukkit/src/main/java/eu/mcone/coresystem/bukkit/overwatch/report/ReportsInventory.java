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
import eu.mcone.coresystem.api.core.overwatch.report.*;
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

import static com.mongodb.client.model.Filters.eq;

public class ReportsInventory extends CoreInventory {

    @Getter
    private final Overwatch overwatch;
    private int currentPage = 0;

    private ReportPriority priority = ReportPriority.EXTREME;
    private boolean live = true;

    private final Map<Integer, Map<ReportPriority, List<AbstractReport>>> pages;

    public ReportsInventory(Overwatch overwatch, Player player) {
        super("§8» §f§lReports", player, InventorySlot.ROW_6);
        this.overwatch = overwatch;
        this.pages = new HashMap<>();

        calculatePages();

        if (pages.size() != 0) {
            int nextPage = currentPage + 1;
            initPriority();
            initReportTyp();
            setPlaceholder();

            if (currentPage >= 1) {
                setItem(InventorySlot.ROW_1_SLOT_2, CategoryInventory.UP_ITEM, e -> constructContent(currentPage - 1));
            }

            constructContent(nextPage);
            if (pages.containsKey(nextPage + 1)) {
                setItem(InventorySlot.ROW_6_SLOT_2, CategoryInventory.DOWN_ITEM, e -> constructContent(currentPage + 1));
            }

            updateItems();
            openInventory();
        } else {
            overwatch.getMessenger().send(player, "§7Momentan sind keine §cReports §7verfügbar!");
        }
    }

    private void initPriority() {
        if (pages.containsKey(currentPage)) {
            int priority = 4;

            for (Map.Entry<ReportPriority, List<AbstractReport>> pageEntry : pages.get(currentPage).entrySet()) {
                if (pageEntry.getValue().size() > 0) {
                    if (pageEntry.getKey().getLevel() < priority) {
                        this.priority = pageEntry.getKey();
                    }
                }
            }
        }
    }

    private void initReportTyp() {
        if (pages.containsKey(currentPage)) {
            live = overwatch.getReportManager().getLiveReportsCollection().countDocuments() > 0;
        }
    }

    private void updateItems() {
        Enchantment enchantment = Enchantment.DURABILITY;

        ItemBuilder builderPriorityExtreme = new ItemBuilder(Material.WOOL, 1, 14).displayName(ReportPriority.EXTREME.getPrefix());
        if (priority == ReportPriority.EXTREME) {
            builderPriorityExtreme.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_1_SLOT_1, builderPriorityExtreme.create(), e -> {
            if (priority != ReportPriority.EXTREME) {
                priority = ReportPriority.EXTREME;
                constructContent(currentPage);
                updateItems();
            }
        });

        ItemBuilder builderPriorityHigh = new ItemBuilder(Material.WOOL, 1, 4).lore().displayName(ReportPriority.HIGH.getPrefix());
        if (priority == ReportPriority.HIGH) {
            builderPriorityHigh.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_2_SLOT_1, builderPriorityHigh.create(), e -> {
            if (priority != ReportPriority.HIGH) {
                priority = ReportPriority.HIGH;
                constructContent(currentPage);
                updateItems();
            }
        });

        ItemBuilder builderPriorityMedium = new ItemBuilder(Material.WOOL, 1, 5).displayName(ReportPriority.MEDIUM.getPrefix());
        if (priority == ReportPriority.MEDIUM) {
            builderPriorityMedium.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_3_SLOT_1, builderPriorityMedium.create(), e -> {
            if (priority != ReportPriority.MEDIUM) {
                priority = ReportPriority.MEDIUM;
                constructContent(currentPage);
                updateItems();
            }
        });

        ItemBuilder builderPriorityNormal = new ItemBuilder(Material.WOOL, 1, 8).displayName(ReportPriority.NORMAL.getPrefix());
        if (priority == ReportPriority.NORMAL) {
            builderPriorityNormal.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_4_SLOT_1, builderPriorityNormal.create(), e -> {
            if (priority != ReportPriority.NORMAL) {
                priority = ReportPriority.NORMAL;
                constructContent(currentPage);
                updateItems();
            }
        });

        ItemBuilder builderLiveReports = new ItemBuilder(Material.WATCH, 1).displayName("§cLive Reports");
        if (live) {
            builderLiveReports.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_6_SLOT_4, builderLiveReports.create(), e -> {
            if (!live) {
                live = true;
                constructContent(currentPage);
                updateItems();
            }
        });

        ItemBuilder builderOpenReports = new ItemBuilder(Material.REDSTONE_COMPARATOR, 1).displayName("§aOffene Reports");
        if (!live) {
            builderOpenReports.enchantment(enchantment, 1).itemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        setItem(InventorySlot.ROW_6_SLOT_6, builderOpenReports.create(), e -> {
            if (live) {
                live = false;
                constructContent(currentPage);
                updateItems();
            }
        });

        setItem(InventorySlot.ROW_6_SLOT_1, CategoryInventory.REFRESH_ITEM, e -> {
            constructContent(currentPage);
            updateItems();
        });

        player.updateInventory();
    }

    private void constructContent(int page) {
        currentPage = page;

        int slot = InventorySlot.ROW_1_SLOT_3;
        double currentRow = 0;
        double row;

        setPlaceholder();

        if (pages.containsKey(page)) {
            if (pages.get(page).containsKey(priority)) {
                for (AbstractReport report : pages.get(page).get(priority)) {
                    if (slot <= 30) {
                        setItem(slot, getItem(report), e -> {
                            BukkitCoreSystem.getSystem().getChannelHandler().createSetRequest(player, "REPORT", "ACCEPT", report.getReportID(), player.getUniqueId().toString());
                            player.closeInventory();
                        });

                        slot++;
                        row = slot / 8.0;

                        if (row > currentRow) {
                            currentRow = row;
                            slot += 3;
                        }
                    }
                }
            } else {
                setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.BARRIER, 1).displayName("§cKeine Reports verfügbar").create());
            }
        }

        player.updateInventory();
    }

    private void calculatePages() {
        int i = 0;
        int page = 1;

        for (LiveReport liveReport : overwatch.getReportManager().getLiveReportsCollection().find()) {
            liveReport.setLive(true);

            if (pages.containsKey(page)) {
                if (pages.get(page).containsKey(liveReport.getPriority())) {
                    pages.get(page).get(liveReport.getPriority()).add(liveReport);
                } else {
                    pages.get(page).put(liveReport.getPriority(), new ArrayList<AbstractReport>() {{
                        add(liveReport);
                    }});
                }
            } else {
                pages.put(page, new HashMap<ReportPriority, List<AbstractReport>>() {{
                    put(liveReport.getPriority(), new ArrayList<AbstractReport>() {{
                        add(liveReport);
                    }});
                }});
            }

            if (priority.getLevel() < liveReport.getPriority().getLevel()) {
                this.priority = liveReport.getPriority();
            }

            if (i >= 28) {
                i = 0;
                ++page;
            } else {
                ++i;
            }
        }

        for (Report report : overwatch.getReportManager().getReportsCollection().find(eq("state", ReportState.OPEN.toString()))) {
            if (pages.containsKey(page)) {
                if (pages.get(page).containsKey(report.getPriority())) {
                    pages.get(page).get(report.getPriority()).add(report);
                } else {
                    pages.get(page).put(report.getPriority(), new ArrayList<AbstractReport>() {{
                        add(report);
                    }});
                }
            } else {
                pages.put(page, new HashMap<ReportPriority, List<AbstractReport>>() {{
                    put(report.getPriority(), new ArrayList<AbstractReport>() {{
                        add(report);
                    }});
                }});
            }

            if (priority.getLevel() < report.getPriority().getLevel()) {
                this.priority = report.getPriority();
            }

            if (i >= 28) {
                i = 0;
                ++page;
            } else {
                ++i;
            }
        }
    }

    private void setPlaceholder() {
        ItemStack itemStack = new ItemStack(Material.AIR, 1);

        setItem(InventorySlot.ROW_1_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_1_SLOT_3, itemStack);
        setItem(InventorySlot.ROW_1_SLOT_4, itemStack);
        setItem(InventorySlot.ROW_1_SLOT_5, itemStack);
        setItem(InventorySlot.ROW_1_SLOT_6, itemStack);
        setItem(InventorySlot.ROW_1_SLOT_7, itemStack);
        setItem(InventorySlot.ROW_1_SLOT_8, itemStack);
        setItem(InventorySlot.ROW_1_SLOT_9, itemStack);

        setItem(InventorySlot.ROW_2_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_2_SLOT_3, itemStack);
        setItem(InventorySlot.ROW_2_SLOT_4, itemStack);
        setItem(InventorySlot.ROW_2_SLOT_5, itemStack);
        setItem(InventorySlot.ROW_2_SLOT_6, itemStack);
        setItem(InventorySlot.ROW_2_SLOT_7, itemStack);
        setItem(InventorySlot.ROW_2_SLOT_8, itemStack);
        setItem(InventorySlot.ROW_2_SLOT_9, itemStack);

        setItem(InventorySlot.ROW_3_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_3_SLOT_3, itemStack);
        setItem(InventorySlot.ROW_3_SLOT_4, itemStack);
        setItem(InventorySlot.ROW_3_SLOT_5, itemStack);
        setItem(InventorySlot.ROW_3_SLOT_6, itemStack);
        setItem(InventorySlot.ROW_3_SLOT_7, itemStack);
        setItem(InventorySlot.ROW_3_SLOT_8, itemStack);
        setItem(InventorySlot.ROW_3_SLOT_9, itemStack);

        setItem(InventorySlot.ROW_4_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_4_SLOT_3, itemStack);
        setItem(InventorySlot.ROW_4_SLOT_4, itemStack);
        setItem(InventorySlot.ROW_4_SLOT_5, itemStack);
        setItem(InventorySlot.ROW_4_SLOT_6, itemStack);
        setItem(InventorySlot.ROW_4_SLOT_7, itemStack);
        setItem(InventorySlot.ROW_4_SLOT_8, itemStack);
        setItem(InventorySlot.ROW_4_SLOT_9, itemStack);

        setItem(InventorySlot.ROW_5_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_3, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_4, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_5, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_6, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_7, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_8, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_5_SLOT_9, CoreInventory.PLACEHOLDER_ITEM);

        setItem(InventorySlot.ROW_6_SLOT_2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_3, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_5, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_7, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_8, CoreInventory.PLACEHOLDER_ITEM);
        setItem(InventorySlot.ROW_6_SLOT_9, CoreInventory.PLACEHOLDER_ITEM);

        if (currentPage >= 1) {
            setItem(InventorySlot.ROW_1_SLOT_2, CategoryInventory.UP_ITEM, e -> constructContent(currentPage - 1));
        }

        if (pages.containsKey(currentPage + 1)) {
            setItem(InventorySlot.ROW_6_SLOT_2, CategoryInventory.DOWN_ITEM, e -> constructContent(currentPage + 1));
        }
    }

    private ItemStack getItem(AbstractReport report) {
        try {
            OfflineCorePlayer reporter = BukkitCoreSystem.getSystem().getOfflineCorePlayer(report.getReporter().get(0));
            OfflineCorePlayer reported = BukkitCoreSystem.getSystem().getOfflineCorePlayer(report.getReported());

            return ItemBuilder.wrap(overwatch.getReportManager().getItemForReason(report.getReportReason()))
                    .displayName("§l" + report.getPriority().getLabel() + reported.getName())
                    .lore(
                            "§7Reportet um: §e" + new SimpleDateFormat("HH:mm").format(new Date(report.getTimestamp() * 1000)),
                            "§7Grund: §e" + report.getReportReason().getName(),
                            "§7Priorität: " + report.getPriority().getPrefix(),
                            "§7Reporteter Spieler: §e" + reporter.getName(),
                            "",
                            "§8» §7Linksklick §8| §7§oAnnehmen"
                    ).create();
        } catch (PlayerNotResolvedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
