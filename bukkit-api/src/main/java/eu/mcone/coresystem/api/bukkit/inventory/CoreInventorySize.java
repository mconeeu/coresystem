/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.inventory;

public enum CoreInventorySize {

    ROW_1(8),
    ROW_2(17),
    ROW_3(26),
    ROW_4(35),
    ROW_5(45),
    ROW_6(54);

    private int value;

    CoreInventorySize(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
