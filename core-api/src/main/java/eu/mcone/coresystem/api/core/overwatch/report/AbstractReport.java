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

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@BsonDiscriminator
public abstract class AbstractReport implements Serializable {

    private String reportID;
    private long timestamp;
    private UUID reported;
    private List<UUID> reporter;
    private ReportReason reportReason;
    private int reportPoints;
    @Setter
    private transient boolean live = false;

    @BsonCreator
    public AbstractReport(String reportID, long timestamp, UUID reported, List<UUID> reporter, ReportReason reportReason, int reportPoints) {
        this.reportID = reportID;
        this.timestamp = timestamp;
        this.reported = reported;
        this.reporter = reporter;
        this.reportReason = reportReason;
        this.reportPoints = reportPoints;
    }

    public void addPoints(int reportPoints) {
        this.reportPoints += reportPoints;
    }

    public ReportPriority getPriority() {
        return ReportPriority.getWherePointsLevel(reportPoints);
    }
}
