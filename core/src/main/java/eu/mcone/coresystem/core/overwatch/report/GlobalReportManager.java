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
import group.onegaming.networkmanager.core.api.database.Database;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
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

    protected final MongoCollection<LiveReport> liveReportsCollection;
    protected final MongoCollection<Report> reportsCollection;

    //TODO: Implementing Ban-/Mute Manager for the TrustedUser System
    protected GlobalReportManager(GlobalOverwatch overwatch, GlobalCoreSystem instance) {
        liveReportsCollection = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).withCodecRegistry(
                fromRegistries(getDefaultCodecRegistry(), fromProviders(new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY), PojoCodecProvider.builder().conventions(Conventions.DEFAULT_CONVENTIONS).automatic(true).build()))
        ).getCollection("overwatch_live_reports", LiveReport.class);
        reportsCollection = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).withCodecRegistry(
                fromRegistries(getDefaultCodecRegistry(), fromProviders(new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY), PojoCodecProvider.builder().conventions(Conventions.DEFAULT_CONVENTIONS).automatic(true).build()))
        ).getCollection("overwatch_reports", Report.class);
    }

    /**
     * Returns all LiveReports
     *
     * @return Map
     */
    @Override
    public List<LiveReport> getLiveReports() {
        List<LiveReport> result = new ArrayList<>();
        for (LiveReport liveReport : liveReportsCollection.find()) {
            result.add(liveReport);
        }

        return result;
    }

    /**
     * Returns all LiveReports sorted by priority
     *
     * @return Map
     */
    @Override
    public Map<Integer, List<LiveReport>> getLiveReportsSortedByLevel() {
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
    @Override
    public LiveReport getLiveReport(String reportID) {
        return liveReportsCollection.find(eq("reportID", reportID)).first();
    }

    /**
     * Returns a live Report object for the specified Team Member UUID
     *
     * @param reported Team Member
     * @return Report
     */
    @Override
    public LiveReport getLiveReport(UUID reported) {
        return liveReportsCollection.find(eq("reported", reported)).first();
    }

    /**
     * Returns all Reports with a specific state
     *
     * @return Map
     */
    @Override
    public List<Report> getReports(ReportState state) {
        List<Report> result = new ArrayList<>();
        for (Report report : reportsCollection.find(eq("state", state.toString()))) {
            result.add(report);
        }

        return result;
    }

    /**
     * Returns a Report object for the specified report ID
     *
     * @param reportID UniqueID
     * @return LiveReport
     */
    @Override
    public Report getReport(String reportID) {
        return reportsCollection.find(eq("reportID", reportID)).first();
    }

    /**
     * Returns a live Report object for the specified Team Member UUID
     *
     * @param reported Team Member
     * @return Report
     */
    @Override
    public Report getReport(UUID reported) {
        return reportsCollection.find(eq("reported", reported)).first();
    }

    /**
     * Counts all currently OPEN Reports
     *
     * @return open report as long
     */
    @Override
    public long getOpenReportsCount() {
        return getLiveReportsCount() + reportsCollection.countDocuments(eq("state", ReportState.OPEN.toString()));
    }

    /**
     * Counts all live Reports
     *
     * @return open report as long
     */
    @Override
    public long getLiveReportsCount() {
        return liveReportsCollection.countDocuments();
    }

    /**
     * Checks if the report is already taken
     *
     * @param reportID ReportID
     * @return boolean
     */
    @Override
    public boolean isReportAlreadyTaken(String reportID) {
        Report report = reportsCollection.find(eq("reportID", reportID)).first();

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
    @Override
    public boolean currentlyWorkingOnReport(UUID uuid) {
        return reportsCollection.find(combine(eq("teamMember", uuid), eq("state", ReportState.IN_PROGRESS.toString()))).first() != null;
    }

    /**
     * Returns the Report the Team Member is currently working on.
     *
     * @param uuid Team Member
     * @return Report
     */
    @Override
    public Report getCurrentlyEditing(UUID uuid) {
        return reportsCollection.find(combine(eq("teamMember", uuid), eq("state", ReportState.IN_PROGRESS.toString()))).first();
    }

    /**
     * Checks if the players was reported by another Player
     *
     * @param uuid reported Player
     * @return boolean
     */
    @Override
    public boolean wasPlayerReported(UUID uuid) {
        return liveReportsCollection.find(eq("reported", uuid)).first() != null;
    }

}
