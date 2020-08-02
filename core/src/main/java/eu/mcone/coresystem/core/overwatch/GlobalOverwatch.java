/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.overwatch;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import lombok.Getter;

@Getter
public class GlobalOverwatch implements eu.mcone.coresystem.api.core.overwatch.GlobalOverwatch {

    private final GlobalCoreSystem instance;

    public GlobalOverwatch(GlobalCoreSystem instance) {
        this.instance = instance;
    }
}
