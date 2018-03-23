/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.lib.gamemode;

import lombok.Getter;

public enum Gamemode {

    BEDWARS("§c§lBedwars"),
    SKYPVP("§9§lSkyPvP"),
    KNOCKIT("§2§lKnockIT"),
    MINEWAR("§5§lMinewar"),
    BUILD("§e§lBuild");

    @Getter
    String label;

    Gamemode(String label) {
        this.label = label;
    }

}
