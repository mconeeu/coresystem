/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import lombok.Getter;

public class NpcModule {

    @Getter
    private static NpcModule instance;
    @Getter
    private final GlobalCoreSystem coreSystem;

    public NpcModule(GlobalCoreSystem coreSystem) {
        instance = this;
        this.coreSystem = coreSystem;
    }

}
