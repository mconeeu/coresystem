/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@BsonDiscriminator
public class Report extends AbstractReport {

    @Setter
    private UUID teamMember;
    private String server;
    @Setter
    private String actionID;
    @Setter
    private ReportState state;
    private Map<String, String> updates;

    public Report(String reportID, UUID teamMember, UUID reported, List<UUID> reporter, ReportReason reportReason, String server, long timestamp, Map<String, String> updates, int reportPoints) {
        super(reportID, timestamp, reported, reporter, reportReason, reportPoints);
        this.teamMember = teamMember;
        this.server = server;
        this.state = ReportState.IN_PROGRESS;
        this.updates = updates;
    }

    @BsonCreator
    public Report(@BsonProperty("reportID") String reportID, @BsonProperty("timestamp") long timestamp, @BsonProperty("reported") UUID reported, @BsonProperty("reporter") List<UUID> reporter,
                  @BsonProperty("reportReason") ReportReason reportReason, @BsonProperty("teamMember") UUID teamMember, @BsonProperty("server") String server,
                  @BsonProperty("actionID") String actionID, @BsonProperty("state") ReportState state,
                  @BsonProperty("updates") Map<String, String> updates, @BsonProperty("reportPoints") int reportPoints) {
        super(reportID, timestamp, reported, reporter, reportReason, reportPoints);
        this.teamMember = teamMember;
        this.server = server;
        this.actionID = actionID;
        this.state = state;
        this.updates = updates;
    }

    public Report(UUID teamMember, LiveReport liveReport) {
        super(liveReport.getReportID(), liveReport.getTimestamp(), liveReport.getReported(), liveReport.getReporter(), liveReport.getReportReason(), liveReport.getReportPoints());
        this.teamMember = teamMember;
        this.server = liveReport.getServer();
        this.state = ReportState.IN_PROGRESS;
        this.updates = new HashMap<>();
    }

    public Report addUpdate(String update) {
        updates.put(String.valueOf(System.currentTimeMillis() / 1000), update);
        return this;
    }
}
