/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerSettingsChange implements Listener {

    @EventHandler
    public void on(PlayerSettingsChangeEvent e) {
        ((BukkitCorePlayer) e.getPlayer()).setSettings(e.getSettings());
    }

}
