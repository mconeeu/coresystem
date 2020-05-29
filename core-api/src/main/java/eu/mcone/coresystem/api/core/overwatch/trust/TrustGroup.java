/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.trust;

import lombok.Getter;

@Getter
public enum TrustGroup {

    HIGH(1, "§a§lHoch", 60, 100, 20), //60% - 100%
    NORMAL(2, "§7§lNormal", 35, 60, 15), //35% - 60%
    LOW(3, "§c§lNiedrig", 0, 35, 10); //0 - 35%

    private final int rank;
    private final String prefix;
    private final int trustPoints;

    private final int min;
    private final int max;

    TrustGroup(int rank, String prefix, int min, int max, int trustPoints) {
        this.rank = rank;
        this.prefix = prefix;
        this.min = min;
        this.max = max;
        this.trustPoints = trustPoints;
    }
}
