/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import group.onegaming.networkmanager.core.api.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.*;

@Getter
@BsonDiscriminator
public class Report {

    private final String ID;
    private final long timestamp;

    @Setter
    private UUID member;
    private final UUID reported;
    private final List<UUID> reporter;

    private final Map<String, String> updates;

    private final ReportReason reason;
    @Setter
    private ReportState state;
    private int points;
    //TODO: Implement her the game history (HistoryID)
    @Setter
    private String server;
    @Setter
    private String punishID;
    @Setter
    private String replayID;
    @Setter
    private String chatLogID;

    public Report(UUID reported, UUID reporter, ReportReason reason, int points) {
        this.ID = new Random(6).nextString();
        this.timestamp = System.currentTimeMillis() / 1000;
        this.reported = reported;
        this.reporter = new ArrayList<UUID>() {{
            add(reporter);
        }};

        this.updates = new HashMap<>();

        this.reason = reason;
        this.points = points;
        this.state = ReportState.OPEN;
    }

    public Report(UUID reported, UUID reporter, ReportReason reason, int points, String replayID) {
        this.ID = new Random(6).nextString();
        this.timestamp = System.currentTimeMillis() / 1000;
        this.reported = reported;
        this.reporter = new ArrayList<UUID>() {{
            add(reporter);
        }};

        this.updates = new HashMap<>();

        this.reason = reason;
        this.points = points;
        this.state = ReportState.OPEN;
        this.replayID = replayID;
    }

    @BsonCreator
    public Report(@BsonProperty("iD") String ID,
                  @BsonProperty("timestamp") long timestamp,
                  @BsonProperty("member") UUID member,
                  @BsonProperty("reported") UUID reported,
                  @BsonProperty("reporter") List<UUID> reporter,
                  @BsonProperty("updates") Map<String, String> updates,
                  @BsonProperty("reason") ReportReason reason,
                  @BsonProperty("state") ReportState state,
                  @BsonProperty("points") int points,
                  @BsonProperty("server") String server,
                  @BsonProperty("punishID") String punishID,
                  @BsonProperty("replayID") String replayID,
                  @BsonProperty("chatLogID") String chatLogID) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.member = member;
        this.reported = reported;
        this.reporter = reporter;
        this.updates = updates;
        this.reason = reason;
        this.state = state;
        this.points = points;
        this.server = server;
        this.punishID = punishID;
        this.replayID = replayID;
        this.chatLogID = chatLogID;
    }

//    @BsonIgnore
//    public ReportState getState() {
//        return ReportState.valueOf(state);
//    }
//
//    @BsonIgnore
//    public void setState(ReportState state) {
//        this.state = state.toString();
//    }

    @BsonIgnore
    public void addPoints(int reportPoints) {
        this.points += reportPoints;
    }

    @BsonIgnore
    public ReportPriority getPriority() {
        return ReportPriority.getLevelWherePoints(points);
    }

    @BsonIgnore
    public void addReporter(UUID uuid) {
        reporter.add(uuid);
    }

    @BsonIgnore
    public Report addUpdate(String update) {
        updates.put(String.valueOf(System.currentTimeMillis() / 1000), update);
        return this;
    }
}
