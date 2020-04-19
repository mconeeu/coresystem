/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.labymod;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public final class LabyModConnection {

    private final UUID playerUuid;
    private final String modVersion;
    private final boolean chunkCachingEnabled;
    private final int chunkCachingVersion;
    private final List<Addon> addons;

}
