/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.punish;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum PunishTemplate {

    RADIKALISMUS("RM", "Radikalismus", 10, 20),

    CLIENTMODS("CM", "Clientmods", 10, 0),
    BETRUG("BT", "Betrug", 10, 0),
    BUGUSING("BU", "Bugusing", 2, 0),
    TEAM_TROLLING("TT", "TeamTrolling", 2, 0),
    TEAMING("T", "Teaming", 1, 2),
    SPAWN_TRAPPING("ST", "SpawnTrapping", 1, 0),
    ERSCHEINEN("ES", "Erscheinen", 1, 0),

    MOBBING("MB", "Mobbing", 5, 5),
    DROHUNG("DH", "Drohung", 5, 5),
    BELEIDIGUNG("BL", "Beleidigung", 0, 2),
    SPAM("SP", "Spam", 0, 2),
    WERBUNG("WB", "Werbung", 0, 2);

    @Getter
    private final int banPoints, mutePoints;
    @Getter
    private final String id, name;

    PunishTemplate(String id, String name, int banPoints, int mutePoints) {
        this.id = id;
        this.name = name;
        this.banPoints = banPoints;
        this.mutePoints = mutePoints;
    }

    public List<PunishTyp> getTypes() {
        ArrayList<PunishTyp> types = new ArrayList<>();

        if (banPoints != 0) {
            types.add(PunishTyp.BAN);
        }

        if (mutePoints != 0) {
            types.add(PunishTyp.MUTE);
        }

        return types;
    }

    public static PunishTemplate getTemplateByID(String id) {
        for (PunishTemplate r : values()) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public enum PunishTyp {
        MUTE,
        BAN
    }
}
