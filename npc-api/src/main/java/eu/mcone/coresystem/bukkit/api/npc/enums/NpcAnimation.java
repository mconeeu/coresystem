/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.api.npc.enums;

import lombok.Getter;

public enum NpcAnimation {

    SWING_ARM(0),
    TAKE_DAMAGE(1),
    LEAVE_BED(2),
    EAT_FOOD(3),
    CRITICAL_EFFECT(4),
    MAGIC_CRITICAL_EFFECT(5);

    @Getter
    private int id;

    NpcAnimation(int id) {
        this.id = id;
    }

}
