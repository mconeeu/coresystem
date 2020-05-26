/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class Statistic implements Serializable {
    private long timestamp;
    private String statisticID;
    @Setter
    private int reports;
    @Setter
    private int bans;
    @Setter
    private int mutes;
    @Setter
    private int botAttacks;

    public Statistic(long timestamp, String statisticID) {
        this.timestamp = timestamp;
        this.statisticID = statisticID;
    }

    public void addReport() {
        reports += 1;
    }

    public void addBan() {
        bans += 1;
    }

    public void addMute() {
        mutes += 1;
    }

    public void addBotAttacks(int botAttacks) {
        this.botAttacks += botAttacks;
    }
}
