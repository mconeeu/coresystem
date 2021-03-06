/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public final class WorldProperties {

    private boolean autoSave, pvp, allowAnimals, allowMonsters, keepSpawnInMemory;

    @Override
    public String toString() {
        return "WorldProperties{" +
                "autoSave=" + autoSave +
                ", pvp=" + pvp +
                ", allowAnimals=" + allowAnimals +
                ", allowMonsters=" + allowMonsters +
                ", keepSpawnInMemory=" + keepSpawnInMemory +
                '}';
    }
}
