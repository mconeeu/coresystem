/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.util.IDUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@BsonDiscriminator
public class LiveReport extends AbstractReport {

    private boolean onceAlreadyPunished;
    @Setter
    private String server;

    public LiveReport(long timestamp, UUID reported, List<UUID> reporter, ReportReason reportReason, boolean onceAlreadyPunished, String server, int reportPoints) {
        this(IDUtils.generateID(), timestamp, reported, reporter, reportReason, onceAlreadyPunished, server, reportPoints);
    }

    @BsonCreator
    public LiveReport(@BsonProperty("reportID") String reportID, @BsonProperty("timestamp") long timestamp, @BsonProperty("reported") UUID reported,
                      @BsonProperty("reporter") List<UUID> reporter, @BsonProperty("reportReason") ReportReason reportReason,
                      @BsonProperty("onceAlreadyPunished") boolean onceAlreadyPunished, @BsonProperty("server") String server, @BsonProperty("reportPoints") int reportPoints) {
        super(reportID, timestamp, reported, reporter, reportReason, reportPoints);

        this.onceAlreadyPunished = onceAlreadyPunished;
        this.server = server;
    }

    public void addReporter(UUID uuid, int points) {
        getReporter().add(uuid);
        addPoints(points);
    }

    public void addReporter(GlobalCorePlayer corePlayer) {
        getReporter().add(corePlayer.getUuid());
        addPoints(corePlayer.getTrust().getGroup().getTrustPoints());
    }

    public Report convertToReport(UUID teamMember) {
        return new Report(teamMember, this);
    }

}
