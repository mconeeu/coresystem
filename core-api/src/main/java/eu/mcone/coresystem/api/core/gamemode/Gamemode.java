/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.gamemode;


import lombok.Getter;

public enum Gamemode {

    BEDWARS("§c§lBedwars"),
    SKYPVP("§9§lSkyPvP"),
    KNOCKIT("§2§lKnockIT"),
    MINEWAR("§5§lMinewar"),
    BUILD("§e§lBuild");

    @Getter
    private String label;

    Gamemode(String label) {
        this.label = label;
    }

}
