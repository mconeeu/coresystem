/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerSettingsChange implements Listener {

    @EventHandler
    public void on(PlayerSettingsChangeEvent e) {
        ((BungeeCorePlayer) e.getPlayer()).setSettings(e.getSettings());
    }

}
