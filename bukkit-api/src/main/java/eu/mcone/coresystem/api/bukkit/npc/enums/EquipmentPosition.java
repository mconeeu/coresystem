/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.enums;

import lombok.Getter;

public enum EquipmentPosition {

    HAND(0),
    BOOTS(1),
    LEGGINS(2),
    CHESTPLATE(3),
    HELMET(4);

    @Getter
    private int id;

    EquipmentPosition(int id) {
        this.id = id;
    }

}
