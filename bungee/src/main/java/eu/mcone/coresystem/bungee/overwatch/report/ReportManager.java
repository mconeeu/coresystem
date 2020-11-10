/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.overwatch.report;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
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
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ReportManager extends GlobalReportManager implements eu.mcone.coresystem.api.bungee.overwatch.report.ReportManager {

    private final Overwatch overwatch;

    private final Map<String, Report> openReports;
    @Getter
    private final Map<UUID, String> inProgress;
    @Getter
    private final HashSet<UUID> loggedIn;

    public ReportManager(Overwatch overwatch) {
        super(BungeeCoreSystem.getInstance());
        this.overwatch = overwatch;
        openReports = new HashMap<>();
        inProgress = new HashMap<>();
        loggedIn = new HashSet<>();
    }

    public void sendOpenReports(ProxiedPlayer player) {
        CorePlayer corePlayer = BungeeCoreSystem.getSystem().getCorePlayer(player);

        if (corePlayer.hasPermission("system.bungee.overwatch.report") && overwatch.isLoggedIn(player)) {
            long open = countOpenReports();
            if (open > 0) {
                if (open == 1) {
                    overwatch.getMessenger().send(player, "§7Es ist momentan §a1 §7Report §aoffen");
                } else {
                    overwatch.getMessenger().send(player, "§7Es sind momentan §f§l" + open + " §cReports §aoffen");
                }
            } else {
                overwatch.getMessenger().send(player, "§7Momentan sind keine Reports §aoffen");
            }
        }
    }

    public void updateCache() {
        openReports.clear();

        for (Report report : reportsCollection.find(eq("state", ReportState.OPEN.toString()))) {
            openReports.put(report.getID(), report);
        }
    }

    public void updateReportData(String id) {
        if (existsReport(id)) {
            Report report = reportsCollection.find(and(eq("iD", id), eq("state", ReportState.OPEN.toString()))).first();

            if (report != null) {
                openReports.put(report.getID(), report);
            }
        }
    }

    /**
     * Adds an LiveReport from the database to the local storage and sends a message to the team members
     *
     * @param id ReportID
     */
    public void addReport(String id) {
        Report report = getReport(id);

        if (report != null) {
            openReports.put(report.getID(), report);
            ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(report.getReporter().get(0));
            ProxiedPlayer reported = ProxyServer.getInstance().getPlayer(report.getReported());

            for (CorePlayer corePlayer : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                if (corePlayer.hasPermission("system.bungee.overwatch.report") && overwatch.isLoggedIn(corePlayer.bungee())) {
                    overwatch.getMessenger().send(corePlayer.bungee(), "§7Der Spieler §e" + reporter.getName() + " §7hat §e" + reported.getName() + " §7reportet §8(§7ID: §e" + report.getID() + "§8). §7Grund: §c" + report.getReason().getName() + " §7Priorität: §c" + report.getPriority().getPrefix());
                    overwatch.getMessenger().send(corePlayer.bungee(),
                            new ComponentBuilder("[ANNEHMEN]")
                                    .color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Zum Server...").create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report accept " + report.getID()))
                                    .create()
                    );

                    int open = (openReports.size() + openReports.size()) - 1;
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
        if (existsReport(id)) {
            Report report = reportsCollection.find(eq("iD", id)).first();

            if (report != null) {
                inProgress.remove(report.getMember());
                report.setMember(null);
                report.setState(ReportState.OPEN);

                reportsCollection.replaceOne(eq("iD", id), report);

                for (CorePlayer corePlayer : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                    if (overwatch.isLoggedIn(corePlayer.bungee())) {
                        overwatch.getMessenger().send(corePlayer.bungee(), "§7Es ist ein neuer Report verfügbar §8(§f§l" + countOpenReports() + " §fVerfügbar§8)");
                    }
                }
            }
        }
    }

    public void closeReport(ProxiedPlayer player) {
        Report report = reportsCollection.find(and(eq("member", player.getUniqueId()), eq("state", ReportState.IN_PROGRESS.toString()))).first();

        if (report != null) {
            inProgress.remove(report.getMember());
            report.setState(ReportState.CLOSED);
            report.addUpdate("Das Teammitglied " + player.getName() + " hat den Report geschlossen!");

            reportsCollection.replaceOne(and(
                    eq("member", player.getUniqueId()),
                    eq("state", ReportState.IN_PROGRESS.toString())
            ), report);

            try {
                for (UUID uuid : report.getReporter()) {
                    OfflineCorePlayer corePlayer = BungeeCoreSystem.getSystem().getOfflineCorePlayer(uuid);
                    corePlayer.increaseWrongReports();
                    overwatch.getTrustManager().checkTrustLvl(uuid);
                }
            } catch (PlayerNotResolvedException e) {
                e.printStackTrace();
            }

            BungeeCoreSystem.getSystem().getChannelHandler().createInfoRequest(player, "REPORT", "CLOSE", report.getID());
        }
    }

    /**
     * Accepts a live report
     *
     * @param id     ReportID
     * @param member The Team Member
     */
    public boolean acceptReport(String id, ProxiedPlayer member) {
        final Report report = reportsCollection.find(and(eq("iD", id), eq("state", ReportState.OPEN.toString()))).first();

        if (inProgress.containsKey(member.getUniqueId())) {
            overwatch.getMessenger().send(member, "§cDu bearbeitest bereits einen Report (ID: " + inProgress.get(member.getUniqueId()) + " )");
            return false;
        } else {
            if (report != null) {
                if (report.getMember() != null) {
                    if (report.getMember().equals(member.getUniqueId())) {
                        overwatch.getMessenger().send(member, "§4Du bearbeitest diesen Report bereits!");
                    } else {
                        try {
                            OfflineCorePlayer corePlayer = BungeeCoreSystem.getSystem().getOfflineCorePlayer(member.getUniqueId());

                            if (corePlayer != null) {
                                overwatch.getMessenger().send(member, "§4Das Teammitglied " + corePlayer.getMainGroup().getColor() + corePlayer.getName() + " bearbeitet diesem Report bereits!");
                            } else {
                                overwatch.getMessenger().send(member, "§4Ein anderes Teammitglied bearbeitet diesen Report bereits!");
                            }
                        } catch (PlayerNotResolvedException e) {
                            e.printStackTrace();
                        }
                    }

                    return false;
                } else {
                    report.setMember(member.getUniqueId());
                    report.setState(ReportState.IN_PROGRESS);
                    report.addUpdate("Das Teammitglied " + member.getName() + " kümmert sich nun um den Report!");
                    reportsCollection.replaceOne(eq("iD", id), report);
                    inProgress.put(member.getUniqueId(), id);

                    try {
                        CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(report.getReported());
                        OfflineCorePlayer offlineCorePlayer = CoreSystem.getInstance().getOfflineCorePlayer(report.getReported());

                        overwatch.getMessenger().sendSimple(member, "" +
                                "\n§8§m----------------§r§8 §eOverwatch §8§m----------------" +
                                "\n§8[§7§l!§8] §7Du kümmerst dich nun um den Report mit der ID §f" + id + "§2." +
                                "\n§8[§7§l!§8] §7Schließe den Report mit §f/report close" +
                                "\n§8[§7§l!§8] §7Reportet um: §e" + new SimpleDateFormat("HH:mm").format(new Date(report.getTimestamp() * 1000)) + "" +
                                "\n§8[§7§l!§8] §7Grund: §e" + report.getReason().getName() + "" +
                                "\n§8[§7§l!§8] §7Server: §e" + (report.getServer() != null ? report.getServer() : "§cX") + "" +
                                "\n§8[§7§l!§8] §7ReplayID: §e" + (report.getReplayID() != null ? report.getReplayID() : "§cX") + "" +
                                "\n§8[§7§l!§8] §7Priorität: §e" + report.getPriority().getPrefix() + "" +
                                "\n§8[§7§l!§8] §7Reporteter Spieler: §e" + (corePlayer != null ? (corePlayer.isNicked() ? corePlayer.getName() + "§8(§5genickt§8)" : corePlayer.getName()) : offlineCorePlayer.getName()) + "" +
                                "\n§8§m----------------------------------------" +
                                "\n");
                    } catch (PlayerNotResolvedException e) {
                        e.printStackTrace();
                    }

                    for (ProxiedPlayer player : overwatch.getLoggedIn()) {
                        if (player != member) {
                            overwatch.getMessenger().send(player, "§a" + member.getName() + " §7kümmert sich nun um den Report §e" + report.getID());
                        }
                    }

                    for (UUID uuid : report.getReporter()) {
                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

                        if (player != null) {
                            overwatch.getMessenger().send(player, "§7Dein Report wird nun §abearbeitet §8(§7ID: " + report.getID() + "§8)");
                        }
                    }

                    return true;
                }
            } else {
                overwatch.getMessenger().send(member, "§4Es konnte kein §aOffener §4Report mit der ID §c" + id + " §4gefunden werden!");
                return false;
            }
        }
    }

    public boolean updateDBEntry(Report report) {
        return reportsCollection.replaceOne(eq("iD", report.getID()), report).getModifiedCount() > 0;
    }
}
