/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.enums;

import lombok.Getter;

public enum NpcAnimation {

    SWING_ARM((byte) 0),
    TAKE_DAMAGE((byte) 1),
    LEAVE_BED((byte) 2),
    EAT_FOOD((byte) 3),
    CRITICAL_EFFECT((byte) 4),
    MAGIC_CRITICAL_EFFECT((byte) 5);

    @Getter
    private final byte id;

    NpcAnimation(byte id) {
        this.id = id;
    }

    public static NpcAnimation getAnimation(byte id) {
        for (NpcAnimation animation : values()) {
            if (animation.getId() == id) {
                return animation;
            }
        }

        return null;
    }
}
