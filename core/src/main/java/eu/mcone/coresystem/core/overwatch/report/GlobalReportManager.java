/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.overwatch.report;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportState;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.Serializable;
import java.util.*;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public abstract class GlobalReportManager implements eu.mcone.coresystem.api.core.overwatch.report.GlobalReportManager, Serializable {

    protected final MongoCollection<Report> reportsCollection;

    //TODO: Implementing Ban-/Mute Manager for the TrustedUser System
    protected GlobalReportManager(GlobalCoreSystem instance) {
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
    public List<Report> getReports(int skip, int limit) {
        List<Report> reports = new ArrayList<>();
        for (Report report : reportsCollection.find().skip(skip).limit(limit)) {
            reports.add(report);
        }

        return reports;
    }

    /**
     * Returns all LiveReports sorted by priority
     *
     * @return Map
     */
    @Override
    public Map<Integer, List<Report>> getOpenReportsSortedByLevel() {
        Map<Integer, List<Report>> sortedReports = new HashMap<>();

        for (Report report : reportsCollection.find(eq("state", ReportState.OPEN.toString()))) {
            if (sortedReports.containsKey(report.getPriority().getLevel())) {
                sortedReports.get(report.getPriority().getLevel()).add(report);
            } else {
                sortedReports.put(report.getPriority().getLevel(), new ArrayList<Report>() {{
                    add(report);
                }});
            }
        }

        return sortedReports;
    }

    /**
     * Returns a live Report object for the specified report ID
     *
     * @param ID UniqueID
     * @return LiveReport
     */
    @Override
    public Report getReport(String ID) {
        return reportsCollection.find(eq("iD", ID)).first();
    }

    /**
     * Returns a live Report object for the specified Team Member UUID
     *
     * @param reported Team Member
     * @return Report
     */
    @Override
    public List<Report> getReports(UUID reported) {
        List<Report> reports = new ArrayList<>();
        for (Report report : reportsCollection.find(eq("reported", reported))) {
            reports.add(report);
        }

        return reports;
    }

    /**
     * Returns all Reports with a specific state
     *
     * @return Map
     */
    @Override
    public List<Report> getReports(ReportState state, int skip, int limit) {
        List<Report> result = new ArrayList<>();
        for (Report report : reportsCollection.find(eq("state", state.toString())).skip(skip).limit(limit)) {
            result.add(report);
        }

        return result;
    }

    /**
     * Returns a live Report object for the specified Team Member UUID
     *
     * @param reported Team Member
     * @return Report
     */
    @Override
    public List<Report> getReports(UUID reported, ReportState state) {
        List<Report> reports = new ArrayList<>();
        for (Report report : reportsCollection.find(and(eq("reported", reported), eq("state", state.toString())))) {
            reports.add(report);
        }

        return reports;
    }

    /**
     * Counts all currently OPEN Reports
     *
     * @return open report as long
     */
    @Override
    public long countOpenReports() {
        return reportsCollection.countDocuments(eq("state", ReportState.OPEN.toString()));
    }

    /**
     * Counts all currently OPEN Reports
     *
     * @return open report as long
     */
    @Override
    public long countReports() {
        return reportsCollection.countDocuments();
    }

    /**
     * Checks if the report is already taken
     *
     * @param ID ReportID
     * @return boolean
     */
    @Override
    public boolean isReportAlreadyTaken(String ID) {
        return reportsCollection.find(and(eq("iD", ID), exists("member", true))).first() != null;
    }

    /**
     * Checks if the player with the specified uuid is currently working on an Report
     *
     * @param uuid Team Member
     * @return boolean
     */
    @Override
    public boolean currentlyWorkingOnReport(UUID uuid) {
        return reportsCollection.find(and(eq("member", uuid), eq("state", ReportState.IN_PROGRESS.toString()))).first() != null;
    }

    public boolean existsReport(String ID) {
        return reportsCollection.find(eq("iD", ID)).first() != null;
    }

    /**
     * Returns the Report the Team Member is currently working on.
     *
     * @param uuid Team Member
     * @return Report
     */
    @Override
    public Report getCurrentlyEditing(UUID uuid) {
        return reportsCollection.find(and(eq("member", uuid), eq("state", ReportState.IN_PROGRESS.toString()))).first();
    }

    /**
     * Checks if the players was reported by another Player
     *
     * @param uuid reported Player
     * @return boolean
     */
    @Override
    public boolean wasPlayerReported(UUID uuid) {
        return reportsCollection.find(eq("reported", uuid)).first() != null;
    }

}
