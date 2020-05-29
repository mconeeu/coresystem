/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import eu.mcone.coresystem.api.core.overwatch.punish.PunishTemplate;
import lombok.Getter;

public enum ReportReason {

    RADIKALISMUS("Radikalismus", 3, PunishTemplate.RADIKALISMUS),
    FLY("Fly", 2, PunishTemplate.CLIENTMODS),
    KILLAURA("Killaura", 2, PunishTemplate.CLIENTMODS),
    SPEED("Speed", 2, PunishTemplate.CLIENTMODS),
    NO_KNOCKBACK("No_Knockback", 2, PunishTemplate.CLIENTMODS),
    SAFE_WALK("Safe_Walk", 2, PunishTemplate.CLIENTMODS),
    AUTO_CLICKER("Auto_Clicker", 2, PunishTemplate.CLIENTMODS),
    NO_SLOWDOWN("No_Slowdown", 2, PunishTemplate.CLIENTMODS),
    BUG_USING("Bug_Using", 1, PunishTemplate.BUGUSING),
    TEAMING("Teaming", 1, PunishTemplate.TEAMING),

    MOBBING("Mobbing", 1, PunishTemplate.MOBBING),
    DROHUNG("Drohung", 1, PunishTemplate.DROHUNG),
    CHAT("Chat", 1, PunishTemplate.SPAM),
    WERBUNG("Werbung", 1, PunishTemplate.WERBUNG),
    TEAM_TROLLING("Team_Trolling", 1, PunishTemplate.TEAM_TROLLING),
    SPAWN_TRAPPING("Spawn_Trapping", 1, PunishTemplate.SPAWN_TRAPPING),
    USERNAME("Nutzername", 1, PunishTemplate.ERSCHEINEN),
    SKIN("Skin", 1, PunishTemplate.ERSCHEINEN),
    BELEIDIGUNG("Beleidigung", 1, PunishTemplate.BELEIDIGUNG);

    @Getter
    private final String name;
    @Getter
    private final int level;
    @Getter
    private final PunishTemplate template;

    ReportReason(String name, int level, PunishTemplate template) {
        this.name = name;
        this.level = level;
        this.template = template;
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
