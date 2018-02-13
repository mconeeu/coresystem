/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

public class PlayerCommandPreprocess implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerCommandPreprocessEvent e) {
        if (!e.isCancelled()) {
            Player player = e.getPlayer();
            String cmd = e.getMessage().split(" ")[0];
            HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);
            if (topic == null) {
                player.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Befehl §c" + cmd + "§4 existiert nicht!");
                e.setCancelled(true);
            }
        }

    }

}
