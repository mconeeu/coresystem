/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.LanguageChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerSettingsChange implements Listener {

    @EventHandler
    public void on(PlayerSettingsChangeEvent e) {
        if (!e.getPlayer().getSettings().getLanguage().equals(e.getSettings().getLanguage())) {
            Bukkit.getPluginManager().callEvent(new LanguageChangeEvent(e.getPlayer(), e.getSettings().getLanguage()));
        }

        ((BukkitCorePlayer) e.getPlayer()).setSettings(e.getSettings());
    }

}
