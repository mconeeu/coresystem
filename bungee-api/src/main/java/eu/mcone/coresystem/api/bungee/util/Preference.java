/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.util;

import lombok.Getter;

public enum Preference {
    MAINTENANCE(false),
    BETA_KEY_SYSTEM(false);

    @Getter
    private Object defaultValue;

    Preference(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
