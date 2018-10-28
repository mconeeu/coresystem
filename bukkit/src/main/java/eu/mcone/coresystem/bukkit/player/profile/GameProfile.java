/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.profile.GameProfileType;
import lombok.Getter;

public abstract class GameProfile implements eu.mcone.coresystem.api.bukkit.player.profile.GameProfile {

    @Getter
    private final String profileName, gameSectionName;
    @Getter
    private final GameProfileType gameProfileType;

    GameProfile(final String profileName, final GameProfileType gameProfileType, final String gameSectionName) {
        this.profileName = profileName;
        this.gameSectionName = gameSectionName;
        this.gameProfileType = gameProfileType;

        CoreSystem.getInstance().getPluginManager().registerGameProfile(this);
    }
}
