/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.report;

import lombok.Getter;

public enum ReportReason {
    FLY("Fly", 2),
    KILLAURA("Killaura", 2),
    SPEED("Speed", 2),
    NO_KNOCKBACK("No_Knockback", 2),
    SAFE_WALK("Safe_Walk", 2),
    AUTO_CLICKER("Auto_Clicker", 2),
    NO_SLOWDOWN("No_Slowdown", 2),
    BUG_USING("Bug_Using", 1),
    TEAMING("Teaming", 1),
    DROHUNG("Drohung", 1),
    TEAM_TROLLING("Team_Trolling", 1),
    SPAWN_TRAPPING("Spawn_Trapping", 1),
    USERNAME("Nutzername", 1),
    SKIN("Skin", 1),
    BELEIDIGUNG("Beleidigung", 1);

    @Getter
    private String name;
    @Getter
    private int level;

    ReportReason(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public static ReportReason getReportReasonByName(String name) {
        for (ReportReason reportReason : values()) {
            if (reportReason.getName().equalsIgnoreCase(name)) {
                return reportReason;
            }
        }
        return null;
    }

}
