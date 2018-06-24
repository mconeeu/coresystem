/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerSettingsChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private BukkitCorePlayer player;
    @Getter
    private PlayerSettings settings;

    public PlayerSettingsChangeEvent(BukkitCorePlayer player, PlayerSettings settings) {
        this.player = player;
        this.settings = settings;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
