/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import com.mongodb.client.MongoCollection;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GlobalReportManager {

    MongoCollection<LiveReport> getLiveReportsCollection();

    MongoCollection<Report> getReportsCollection();

    Map<Integer, List<LiveReport>> getLiveReportsWhereLevel();

    long countOpenReports();

    boolean isReportAlreadyTaken(String reportID);

    boolean currentlyWorkingOnReport(UUID uuid);

    AbstractReport getCurrentlyEditing(UUID uuid);

    boolean wasPlayerReported(UUID uuid);

    LiveReport getLiveReport(String reportID);

    LiveReport getLiveReport(UUID reported);

    Report getReport(String reportID);

    Report getReport(UUID reported);

}
