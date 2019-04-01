/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public final class NpcData {

    private String name, displayname, skinName;
    private SkinKind skinKind = SkinKind.DATABASE;
    private CoreLocation location;

    public enum SkinKind {
        PLAYER, DATABASE
    }

}
