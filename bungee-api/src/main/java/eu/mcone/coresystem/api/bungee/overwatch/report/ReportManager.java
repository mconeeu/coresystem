/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.overwatch.report;

import eu.mcone.coresystem.api.core.overwatch.report.LiveReport;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public interface ReportManager {

    Map<String, LiveReport> getLiveReportsCache();

    Map<String, Report> getOpenReportsCache();

    void sendOpenReports(ProxiedPlayer player);

    void updateCaches();

    void closeReport(ProxiedPlayer player);
}