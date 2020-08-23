/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import com.mongodb.client.FindIterable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GlobalReportManager {

    FindIterable<Report> getReports(int skip, int limit);

    Map<Integer, List<Report>> getOpenReportsSortedByLevel();

    Report getReport(String ID);

    FindIterable<Report> getReports(UUID reported);

    FindIterable<Report> getReports(ReportState state, int skip, int limit);

    FindIterable<Report> getReports(UUID reported, ReportState state);

    long countOpenReports();

    long countReports();

    boolean isReportAlreadyTaken(String ID);

    boolean currentlyWorkingOnReport(UUID uuid);

    boolean existsReport(String ID);

    Report getCurrentlyEditing(UUID uuid);

    boolean wasPlayerReported(UUID uuid);

}
