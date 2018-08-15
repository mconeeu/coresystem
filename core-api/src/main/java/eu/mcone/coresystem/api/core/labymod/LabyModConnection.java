/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.labymod;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public final class LabyModConnection {

    private UUID playerUuid;
    private String modVersion;
    private boolean chunkCachingEnabled;
    private int chunkCachingVersion;
    private List<Addon> addons;

}
