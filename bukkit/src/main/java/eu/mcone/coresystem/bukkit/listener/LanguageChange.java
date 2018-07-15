/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.LanguageChangeEvent;
import eu.mcone.coresystem.bukkit.hologram.HologramManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LanguageChange implements Listener {
    
    @EventHandler
    public void on(LanguageChangeEvent e) {
        e.getPlayer().getScoreboard().reload();
        ((HologramManager) CoreSystem.getInstance().getHologramManager()).reload(e.getPlayer().bukkit());
    }
    
}
