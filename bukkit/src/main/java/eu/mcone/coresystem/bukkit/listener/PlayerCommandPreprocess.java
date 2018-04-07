/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

import java.util.Arrays;

public class PlayerCommandPreprocess implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(PlayerCommandPreprocessEvent e) {
        if (!e.isCancelled()) {
            Player p = e.getPlayer();

            String[] line = e.getMessage().split(" ");
            String cmd = line[0];

            CoreCommand command = CoreSystem.getInstance().getCoreCommand(cmd);

            if (command != null) {
                e.setCancelled(true);
                if (p.hasPermission(command.getPermission())) {
                    command.execute(p, Arrays.copyOfRange(line, 1, line.length));
                } else {
                    p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Du hast keine Berechtigung für diesen Befehl!");
                }
            } else {
                HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);
                if (topic == null) {
                    p.sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§4Der Befehl §c" + cmd + "§4 existiert nicht!");
                    e.setCancelled(true);
                }
            }
        }

    }

}
