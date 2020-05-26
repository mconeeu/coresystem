/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.trust;

import lombok.Getter;

@Getter
public enum TrustGroup {

    //Difference: 25
    HIGH(1, "§a§lHoch", 20),
    //Difference: 50
    NORMAL(2, "§7§lNormal", 15),
    //Difference: 25
    LOW(3, "§c§lNiedrig", 15);

    private transient final int rank;
    private transient final String prefix;
    private transient final int trustPoints;

    TrustGroup(int rank, String prefix, int trustPoints) {
        this.rank = rank;
        this.prefix = prefix;
        this.trustPoints = trustPoints;
    }
}
