/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.event;

import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

public final class PlayerSettingsChangeEvent extends Event {

    @Getter
    private BungeeCorePlayer player;
    @Getter
    private PlayerSettings settings;

    public PlayerSettingsChangeEvent(BungeeCorePlayer player, PlayerSettings settings) {
        this.player = player;
        this.settings = settings;
    }

}
