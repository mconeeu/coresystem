/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.anvil;

import lombok.Getter;

public enum AnvilSlot {

    INPUT_LEFT(0),
    INPUT_RIGHT(1),
    OUTPUT(2);

    @Getter
    private final int slot;

    AnvilSlot(int slot) {
        this.slot = slot;
    }

    public static AnvilSlot bySlot(int slot) {
        for (AnvilSlot anvilSlot : values()) {
            if (anvilSlot.getSlot() == slot) {
                return anvilSlot;
            }
        }

        return null;
    }

}
