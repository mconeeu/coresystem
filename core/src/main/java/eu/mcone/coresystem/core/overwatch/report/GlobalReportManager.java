/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.overwatch.report;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.overwatch.report.LiveReport;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.overwatch.GlobalOverwatch;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.Serializable;
import java.util.*;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public abstract class GlobalReportManager implements eu.mcone.coresystem.api.core.overwatch.report.GlobalReportManager, Serializable {

    private GlobalOverwatch overwatch;
    private final GlobalCoreSystem instance;

    @Getter
    private final MongoCollection<LiveReport> liveReportsCollection;
    @Getter
    private final MongoCollection<Report> reportsCollection;

    //TODO: Implementing Ban-/Mute Manager for the TrustedUser System
    protected GlobalReportManager(GlobalOverwatch overwatch, GlobalCoreSystem instance) {
        this.overwatch = overwatch;
        this.instance = instance;
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),
                fromProviders(
                        new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY),
                        PojoCodecProvider.builder().conventions(Conventions.DEFAULT_CONVENTIONS).automatic(true).build()
                )

        );

        liveReportsCollection = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).withCodecRegistry(codecRegistry).getCollection("overwatch_live_reports", LiveReport.class);
        reportsCollection = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).withCodecRegistry(codecRegistry).getCollection("overwatch_reports", Report.class);
    }

    /**
     * Returns all LiveReports sorted by priority
     *
     * @return Map
     */
    public Map<Integer, List<LiveReport>> getLiveReportsWhereLevel() {
        Map<Integer, List<LiveReport>> sortedReports = new HashMap<>();

        for (LiveReport report : liveReportsCollection.find()) {
            if (sortedReports.containsKey(report.getPriority().getLevel())) {
                sortedReports.get(report.getPriority().getLevel()).add(report);
            } else {
                sortedReports.put(report.getPriority().getLevel(), new ArrayList<LiveReport>() {{
                    add(report);
                }});
            }
        }

        return sortedReports;
    }

    /**
     * Returns a live Report object for the specified report ID
     *
     * @param reportID UniqueID
     * @return LiveReport
     */
    public LiveReport getLiveReport(String reportID) {
        return getLiveReportsCollection().find(eq("reportID", reportID)).first();
    }

    /**
     * Returns a live Report object for the specified Team Member UUID
     *
     * @param reported Team Member
     * @return Report
     */
    public LiveReport getLiveReport(UUID reported) {
        return getLiveReportsCollection().find(eq("reported", reported.toString())).first();
    }

    /**
     * Returns a Report object for the specified report ID
     *
     * @param reportID UniqueID
     * @return LiveReport
     */
    public Report getReport(String reportID) {
        return getReportsCollection().find(eq("reportID", reportID)).first();
    }

    /**
     * Returns a live Report object for the specified Team Member UUID
     *
     * @param reported Team Member
     * @return Report
     */
    public Report getReport(UUID reported) {
        return getReportsCollection().find(eq("reported", reported.toString())).first();
    }

    /**
     * Counts all currently OPEN Reports
     *
     * @return open report as long
     */
    public long countOpenReports() {
        return getLiveReportsCollection().countDocuments() + getReportsCollection().countDocuments(eq("state", ReportState.OPEN.toString()));
    }

    /**
     * Checks if the report is already taken
     *
     * @param reportID ReportID
     * @return boolean
     */
    public boolean isReportAlreadyTaken(String reportID) {
        Report report = getReportsCollection().find(eq("reportID", reportID)).first();

        if (report != null) {
            return report.getTeamMember() != null;
        }

        return false;
    }

    /**
     * Checks if the player with the specified uuid is currently working on an Report
     *
     * @param uuid Team Member
     * @return boolean
     */
    public boolean currentlyWorkingOnReport(UUID uuid) {
        return getReportsCollection().find(combine(eq("teamMember", uuid.toString()), eq("state", ReportState.IN_PROGRESS.toString()))).first() != null;
    }

    /**
     * Returns the Report the Team Member is currently working on.
     *
     * @param uuid Team Member
     * @return Report
     */
    public Report getCurrentlyEditing(UUID uuid) {
        return getReportsCollection().find(combine(eq("teamMember", uuid.toString()), eq("state", ReportState.IN_PROGRESS.toString()))).first();
    }

    /**
     * Checks if the players was reported by another Player
     *
     * @param uuid reported Player
     * @return boolean
     */
    public boolean wasPlayerReported(UUID uuid) {
        return getLiveReportsCollection().find(eq("reported", uuid.toString())).first() != null;
    }
}
