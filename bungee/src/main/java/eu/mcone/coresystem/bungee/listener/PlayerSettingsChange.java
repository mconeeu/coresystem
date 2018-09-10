/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.event.LanguageChangeEvent;
import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerSettingsChange implements Listener {

    @EventHandler
    public void on(PlayerSettingsChangeEvent e) {
        if (!e.getPlayer().getSettings().getLanguage().equals(e.getSettings().getLanguage())) {
            ProxyServer.getInstance().getPluginManager().callEvent(new LanguageChangeEvent(e.getPlayer(), e.getSettings().getLanguage()));
        }
        
        ((GlobalOfflineCorePlayer) e.getPlayer()).setSettings(e.getSettings());
    }

}
