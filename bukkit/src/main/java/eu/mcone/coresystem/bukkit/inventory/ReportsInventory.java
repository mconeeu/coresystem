package eu.mcone.coresystem.bukkit.inventory;

import com.mongodb.client.FindIterable;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.overwatch.Overwatch;
import eu.mcone.coresystem.bukkit.overwatch.report.ReportInfoInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class ReportsInventory extends CategoryInventory {

    private static final ItemStack ALL_REPORTS = new ItemBuilder(Material.FEATHER).displayName("Alle").create();
    private static final ItemStack OPEN_REPORTS = new ItemBuilder(Material.STICK).displayName("Offen").create();
    private static final Map<ItemStack, String> CATEGORIES = new HashMap<ItemStack, String>() {{
        put(ALL_REPORTS, "Alle");
        put(OPEN_REPORTS, "Offen");
    }};

    protected static final Overwatch OVERWATCH = BukkitCoreSystem.getSystem().getOverwatch();

    public ReportsInventory(ItemStack itemStack, Player player) {
        super("§8» §f§lReports §8» " + CATEGORIES.get(itemStack), player, itemStack);

        CATEGORIES.forEach((i, n) -> addCategory(i));

        openInventory();
    }

    public static void openNewInventory(Player player) {
        new ReportsInventory(CATEGORIES.keySet().iterator().next(), player);
    }

    @Override
    protected void openCategoryInventory(ItemStack categoryItem, Player player) {
        new ReportsInventory(categoryItem, player);
    }

    @Override
    public int setPaginatedItems(int skip, int limit, List<CategoryInvItem> items) {
        FindIterable<Report> reports = currentCategoryItem == ALL_REPORTS
                ? OVERWATCH.getReportManager().getReportsCollection().find().skip(skip).limit(limit).sort(descending("points"))
                : OVERWATCH.getReportManager().getReportsCollection().find(eq("state", ReportState.OPEN.toString())).skip(skip).limit(limit).sort(descending("points"));

        if (reports.first() != null) {
            for (Report report : reports) {
                items.add(new CategoryInvItem(getItem(report), e -> {
                    if (report.getState().equals(ReportState.OPEN)) {
                        BukkitCoreSystem.getSystem().getChannelHandler().createSetRequest(player, "REPORT", "ACCEPT", report.getID(), player.getUniqueId().toString());
                        player.closeInventory();
                    } else {
                        new ReportInfoInventory(player, report);
                        player.closeInventory();
                    }
                }));
            }
        } else {
            addItem(new ItemBuilder(Material.BARRIER).displayName("§4Keine Ergebnisse in §c§l" + CATEGORIES.get(currentCategoryItem)).create());
        }

        return (int) (currentCategoryItem == ALL_REPORTS ? OVERWATCH.getReportManager().countReports() : OVERWATCH.getReportManager().countOpenReports());
    }

    protected ItemStack getItem(Report report) {
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
                            "§7Status: §e" + report.getState().getPrefix(),
                            "§7Punkte: §e" + report.getPoints(),
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
