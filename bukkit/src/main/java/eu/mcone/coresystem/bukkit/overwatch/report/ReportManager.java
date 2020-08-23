/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.overwatch.report;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.PlayerReportedEvent;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.LeatherArmorItem;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.overwatch.report.ReportMethod;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportReason;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.overwatch.Overwatch;
import eu.mcone.coresystem.core.overwatch.report.GlobalReportManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.mongodb.client.model.Filters.*;

public class ReportManager extends GlobalReportManager implements eu.mcone.coresystem.api.bukkit.overwatch.report.ReportManager, eu.mcone.coresystem.api.core.overwatch.report.GlobalReportManager {

    private final Overwatch overwatch;

    private final Map<UUID, Report> toConfirm;
    @Getter
    private final EnumMap<ReportReason, ItemStack> reasonItems;
    private BukkitTask task;

    private ReportMethod reportMethod;

    public ReportManager(Overwatch overwatch, GlobalCoreSystem instance) {
        super(instance);
        this.overwatch = overwatch;
        toConfirm = new HashMap<>();

        reasonItems = new EnumMap<ReportReason, ItemStack>(ReportReason.class) {{
            put(ReportReason.FLY, new ItemBuilder(Material.FEATHER, 1).displayName("§fFly").create());
            put(ReportReason.KILLAURA, new ItemBuilder(Material.DIAMOND_SWORD, 1).displayName("§cKillaura").create());
            put(ReportReason.SPEED, new ItemBuilder(Material.POTION, 1).displayName("§bSpeed").create());
            put(ReportReason.NO_KNOCKBACK, new ItemBuilder(Material.ENCHANTED_BOOK, 1).displayName("§7No Knockback").create());
            put(ReportReason.SAFE_WALK, new ItemBuilder(Material.SANDSTONE, 1).displayName("§eSafe Walk").create());
            put(ReportReason.AUTO_CLICKER, new ItemBuilder(Material.IRON_SWORD, 1).displayName("§fAuto Klicker").create());
            put(ReportReason.NO_SLOWDOWN, new ItemBuilder(Material.WEB, 1).displayName("§fNo Slowdown").create());
            put(ReportReason.BUG_USING, new ItemBuilder(Material.BARRIER, 1).displayName("§cBug Using").create());
            put(ReportReason.TEAMING, new LeatherArmorItem(Material.LEATHER_CHESTPLATE).setColor(Color.RED).toItemBuilder().displayName("§cTeaming").create());
            put(ReportReason.DROHUNG, new ItemBuilder(Material.GOLD_SWORD, 1).displayName("§4Drohung").create());
            put(ReportReason.TEAM_TROLLING, new LeatherArmorItem(Material.LEATHER_CHESTPLATE).setColor(Color.BLUE).toItemBuilder().displayName("§7Team Trolling").create());
            put(ReportReason.SPAWN_TRAPPING, new ItemBuilder(Material.BED, 1).displayName("§eSpawn Trapping").create());
            put(ReportReason.USERNAME, new ItemBuilder(Material.NAME_TAG, 1).displayName("§fUsername").create());
            put(ReportReason.SKIN, new Skull("DieserDominik").setDisplayName("§fSkin").getItemStack());
            put(ReportReason.BELEIDIGUNG, new ItemBuilder(Material.MAP, 1).displayName("§cBeleidigung").create());
        }};

        reportMethod = ((manager, reporter, reported, reportReason) -> {
            CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(reporter);
            return new Report(reported.getUniqueId(), reporter.getUniqueId(), reportReason, corePlayer.getTrust().getGroup().getTrustPoints());
        });
    }

    public void setReportMethod(ReportMethod method) {
        this.reportMethod = method;
    }

    public ItemStack getItemForReason(ReportReason reportReason) {
        return reasonItems.getOrDefault(reportReason, null);
    }

    public Collection<Report> getToConfirm() {
        return toConfirm.values();
    }

    public boolean report(Player reporter, Player reported, ReportReason reportReason) {
        Report report = reportsCollection.find(
                and(
                        eq("reported", reported.getUniqueId()),
                        eq("state", ReportState.OPEN.toString()),
                        lt("timestamp", (System.currentTimeMillis() / 1000) - 3600)
                )
        ).first();

        if (report != null) {
            if (report.getReporter().contains(reporter.getUniqueId())) {
                overwatch.getMessenger().send(reported, "§4Du hast den Spieler §f§l" + reported.getName() + " §4bereits Reportet.");
                return false;
            } else {
                run();
                toConfirm.put(reporter.getUniqueId(), report);
                return true;
            }
        } else {
            Report succeed = reportMethod.report(this, reporter, reported, reportReason);
            if (succeed != null) {
                run();
                toConfirm.put(reporter.getUniqueId(), succeed);
            }

            return succeed != null;
        }
    }

    protected void run() {
        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                List<UUID> toRemove = new ArrayList<>();
                for (Map.Entry<UUID, Report> report : toConfirm.entrySet()) {
                    if (((System.currentTimeMillis() / 1000) - report.getValue().getTimestamp()) == 15) {
                        Player player = Bukkit.getPlayer(report.getKey());

                        if (player != null) {
                            overwatch.getMessenger().send(player, "§4Dein Report ist §cabgelaufen!");
                        }

                        toRemove.add(report.getKey());
                    }
                }

                if (!toRemove.isEmpty()) {
                    for (UUID uuid : toRemove) {
                        toConfirm.remove(uuid);
                    }
                }

                if (toConfirm.size() < 1) {
                    task.cancel();
                }
            }, 20, 20);
        }
    }

    public boolean confirmReport(Player player) {
        if (toConfirm.containsKey(player.getUniqueId())) {
            Player reported = Bukkit.getPlayer(player.getUniqueId());
            CorePlayer corePlayer = BukkitCoreSystem.getSystem().getCorePlayer(player);

            Report report = toConfirm.get(player.getUniqueId());
            if (!existsReport(report.getID())) {
                toConfirm.remove(player.getUniqueId());
                reportsCollection.insertOne(report);
                BukkitCoreSystem.getSystem().getChannelHandler().createSetRequest(player, "REPORT", "NEW", report.getID());
                overwatch.getMessenger().send(player, "§7Du hast den Spieler §f§l" + reported.getName() + " §7erfolgreich für §f§l" + report.getReason().getName() + " §7reported.");
            } else {
                report.addReporter(corePlayer.getUuid());
                reportsCollection.replaceOne(eq("ID", report.getID()), report);
                BukkitCoreSystem.getSystem().getChannelHandler().createSetRequest(player, "REPORT", "UPDATE", report.getID());
            }

            Bukkit.getPluginManager().callEvent(new PlayerReportedEvent(report.getID(), report.getReporter(), Bukkit.getPlayer(report.getReported()), report.getReason()));
            return true;
        } else {
            overwatch.getMessenger().send(player, "§4Du musst zuerst einen Spieler reporten um einen Report bestätigen zu können!");
            return false;
        }
    }
}
