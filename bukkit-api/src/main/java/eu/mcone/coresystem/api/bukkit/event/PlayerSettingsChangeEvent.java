/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class PlayerSettingsChangeEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final CorePlayer player;
    private final PlayerSettings settings;

    public PlayerSettingsChangeEvent(CorePlayer player, PlayerSettings settings) {
        this.player = player;
        this.settings = settings;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
