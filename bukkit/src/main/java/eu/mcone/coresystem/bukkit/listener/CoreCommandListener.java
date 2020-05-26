/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

public class CoreCommandListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (!e.isCancelled()) {
            Player p = e.getPlayer();

            String cmd = e.getMessage().split(" ")[0];

            HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);
            if (topic == null) {
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Der Befehl §c" + cmd + "§4 existiert nicht!");
                e.setCancelled(true);
            }
        }

    }

}
