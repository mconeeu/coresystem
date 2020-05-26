/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.overwatch.report;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.report.LiveReport;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import eu.mcone.coresystem.core.overwatch.report.GlobalReportManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class ReportManager extends GlobalReportManager {

    private final Overwatch overwatch;

    @Getter
    private final Map<String, LiveReport> liveReportsCache;
    @Getter
    private final Map<String, Report> openReportsCache;
    @Getter
    private final Map<UUID, String> inProgress;

    public ReportManager(Overwatch overwatch) {
        super(overwatch, BungeeCoreSystem.getInstance());
        this.overwatch = overwatch;
        liveReportsCache = new HashMap<>();
        openReportsCache = new HashMap<>();
        inProgress = new HashMap<>();
    }

    public void sendOpenReports(ProxiedPlayer player) {
        CorePlayer corePlayer = BungeeCoreSystem.getSystem().getCorePlayer(player);

        if (corePlayer.hasPermission("overwatch.report.notification") || corePlayer.hasPermission("overwatch.report.*") && corePlayer.getSettings().isReceiveIncomingReports()) {
            long open = countOpenReports();
            if (open > 0) {
                if (open == 1) {
                    overwatch.getMessenger().send(player, "§7Es ist momentan §a1 §7Report §aoffen");
                } else {
                    overwatch.getMessenger().send(player, "§7Es sind momentan §f§l" + open + " §cReports §aoffen");
                }
            }
        }
    }

    public void updateCaches() {
        liveReportsCache.clear();
        openReportsCache.clear();

        for (LiveReport liveReport : getLiveReportsCollection().find()) {
            liveReportsCache.put(liveReport.getReportID(), liveReport);
        }

        for (Report report : getReportsCollection().find()) {
            if (report.getState().equals(ReportState.OPEN)) {
                openReportsCache.put(report.getReportID(), report);
            }
        }
    }

    public void updateReportData(String id) {
        LiveReport liveReport = getLiveReportsCollection().find(eq("reportID", id)).first();

        if (liveReport != null) {
            liveReportsCache.put(liveReport.getReportID(), liveReport);
        } else {
            Report report = getReportsCollection().find(combine(eq("reportID", id), eq("state", ReportState.OPEN.toString()))).first();
            if (report != null) {
                openReportsCache.put(report.getReportID(), report);
            }
        }
    }

    /**
     * Adds an LiveReport from the database to the local storage and sends a message to the team members
     *
     * @param id ReportID
     */
    public void addLiveReportFromDB(String id) {
        LiveReport liveReport = getLiveReportsCollection().find(eq("reportID", id)).first();

        if (liveReport != null) {
            liveReportsCache.put(liveReport.getReportID(), liveReport);
            ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(liveReport.getReporter().get(0));
            ProxiedPlayer reported = ProxyServer.getInstance().getPlayer(liveReport.getReported());

            for (CorePlayer corePlayer : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                if (corePlayer.hasPermission("overwatch.report.notification") || corePlayer.hasPermission("overwatch.report.*") && corePlayer.getSettings().isReceiveIncomingReports()) {
                    overwatch.getMessenger().send(corePlayer.bungee(), "§7Der Spieler §e" + reporter.getName() + " §7hat §e" + reported.getName() + " §7reportet §8(§7ID: §e" + liveReport.getReportID() + "§8). §7Grund: §c" + liveReport.getReportReason().getName() + " §7Priorität: §c" + liveReport.getPriority().getPrefix());
                    overwatch.getMessenger().send(corePlayer.bungee(),
                            new ComponentBuilder("[ANNEHMEN]")
                                    .color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Zum Server...").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report accept " + liveReport.getReportID()))
                                    .create()
                    );

                    int open = (liveReportsCache.size() + openReportsCache.size()) - 1;
                    if (open > 0) {
                        if (open > 1) {
                            overwatch.getMessenger().send(corePlayer.bungee(), "§7Es sind noch §e§l" + open + " §7Reports offen.");
                        } else {
                            overwatch.getMessenger().send(corePlayer.bungee(), "§7Es ist noch §e§l" + open + " §7Report offen!");
                        }
                    }
                }
            }
        }
    }

    public void removeTeamMember(String id) {
        Report report = getReportsCollection().find(eq("reportID", id)).first();

        if (report != null) {
            report.setTeamMember(null);
            report.setState(ReportState.OPEN);
            getReportsCollection().replaceOne(eq("reportID", id), report);
            openReportsCache.put(id, report);

            for (CorePlayer corePlayer : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                if (corePlayer.hasPermission("overwatch.report.notification") && corePlayer.getSettings().isReceiveIncomingReports()) {
                    overwatch.getMessenger().send(corePlayer.bungee(), "§7Es ist ein neuer Report verfügbar §8(§f§l" + liveReportsCache.size() + openReportsCache.size() + " §fVerfügbar§8)");
                }
            }
        }
    }

    public void closeReport(ProxiedPlayer player) {
        Report report = getReportsCollection().find(combine(eq("teamMember", player.getUniqueId()), eq("state", ReportState.IN_PROGRESS.toString()))).first();

        if (report != null) {
            inProgress.remove(report.getTeamMember());
            report.setState(ReportState.CLOSED);
            report.addUpdate("Das Teammitglied " + player.getName() + " hat den Report geschlossen!");
            getReportsCollection().replaceOne(combine(eq("teamMember", player.getUniqueId()), eq("state", ReportState.IN_PROGRESS.toString())), report);
            BungeeCoreSystem.getSystem().getChannelHandler().createInfoRequest(player, "REPORT", "CLOSE", report.getReportID());
        }
    }

    /**
     * Accepts a live report
     *
     * @param id         ReportID
     * @param teamMember The Team Member
     */
    public boolean acceptReport(String id, ProxiedPlayer teamMember) {
        final Report acceptedReport;
        final LiveReport liveReport = getLiveReportsCollection().find(eq("reportID", id)).first();
        final Report dbReport = getReportsCollection().find(combine(eq("reportID", id), eq("state", ReportState.OPEN.toString()))).first();

        if (dbReport == null) {
            if (liveReport != null) {
                getLiveReportsCollection().deleteOne(eq("reportID", id));
                acceptedReport = liveReport.convertToReport(teamMember.getUniqueId());
                getReportsCollection().insertOne(acceptedReport);
            } else {
                overwatch.getMessenger().send(teamMember, "§4Es konnte kein §aOffener §4Report mit der ID §c" + id + " §4gefunden werden!");
                return false;
            }
        } else if (dbReport.getTeamMember() == null) {
            if (liveReport != null) {
                getLiveReportsCollection().deleteOne(eq("reportID", id));
            }

            acceptedReport = dbReport;
        } else if (dbReport.getTeamMember().equals(teamMember.getUniqueId())) {
            overwatch.getMessenger().send(teamMember, "§4Du bearbeitest diesen Report bereits!");
            BungeeCoreSystem.getInstance().getChannelHandler().createInfoRequest(teamMember, "REPORT", "ACCEPT", "CANCELED");
            return false;
        } else {
            overwatch.getMessenger().send(teamMember, "§4Es konnte kein Report mit der ID §c" + id + " §4gefunden werden!");
            BungeeCoreSystem.getInstance().getChannelHandler().createInfoRequest(teamMember, "REPORT", "ACCEPT", "CANCELED");
            return false;
        }

        acceptedReport.setTeamMember(teamMember.getUniqueId());
        acceptedReport.addUpdate("Das Teammitglied " + teamMember.getName() + " kümmert sich nun um den Report!");
        getReportsCollection().replaceOne(eq("reportID", id), acceptedReport);
        inProgress.put(teamMember.getUniqueId(), id);

        try {
            CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(acceptedReport.getReported());
            OfflineCorePlayer offlineCorePlayer = CoreSystem.getInstance().getOfflineCorePlayer(acceptedReport.getReported());

            overwatch.getMessenger().sendSimple(teamMember, "" +
                    "\n§8§m----------------§r§8 §eOverwatch §8§m----------------" +
                    "\n§8[§7§l!§8] §7Du kümmerst dich nun um den Report mit der ID §f" + id + "§2." +
                    "\n§8[§7§l!§8] §7Schließe den Report mit §f/report close" +
                    "\n§8[§7§l!§8] §7Reportet um: §e" + new SimpleDateFormat("HH:mm").format(new Date(acceptedReport.getTimestamp() * 1000)) + "" +
                    "\n§8[§7§l!§8] §7Grund: §e" + acceptedReport.getReportReason().getName() + "" +
                    "\n§8[§7§l!§8] §7Server: §e" + acceptedReport.getServer() + "" +
                    "\n§8[§7§l!§8] §7Reporteter Spieler: §e" + (corePlayer != null ? (corePlayer.isNicked() ? corePlayer.getName() + "§8(§5genickt§8)" : corePlayer.getName()) : offlineCorePlayer.getName()) + "" +
                    "\n§8§m----------------------------------------" +
                    "\n");


        } catch (PlayerNotResolvedException e) {
            e.printStackTrace();
        }

        for (UUID uuid : acceptedReport.getReporter()) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

            if (player != null) {
                overwatch.getMessenger().send(player, "§7Dein Report wird nun §abearbeitet §8(§7ID: " + acceptedReport.getReportID() + "§8)");
            }
        }

        BungeeCoreSystem.getInstance().getChannelHandler().createInfoRequest(teamMember, "REPORT", "ACCEPT", acceptedReport.getReportID(), teamMember.getUniqueId().toString());
        return false;
    }
}
