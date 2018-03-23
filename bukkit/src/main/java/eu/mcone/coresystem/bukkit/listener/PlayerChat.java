/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener{
	
	@EventHandler
	public void on(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		CorePlayer cp = CoreSystem.getCorePlayer(p);
		String msg = e.getMessage();
		String prefix;

		if (CoreSystem.cfg.getConfig().getBoolean("UserChat")){
            if (cp.isNicked()) {
                prefix = Group.SPIELER.getPrefix() + CoreSystem.config.getConfigValue("Chat-Design").replaceAll("%Player%", p.getName());
            } else {
				prefix = cp.getMainGroup().getPrefix() + CoreSystem.config.getConfigValue("Chat-Design").replaceAll("%Player%", p.getName());
            }

			for (Player receiver : Bukkit.getOnlinePlayers()) {
				if (msg.contains("@" + receiver.getName())){
					e.getRecipients().remove(receiver);
					receiver.sendMessage(prefix.replaceAll("Nachricht", msg.replaceAll("@" + receiver.getName(), ChatColor.AQUA + "@" + receiver.getName() + ChatColor.GRAY)));
                    receiver.playSound(receiver.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
				} else if (msg.contains(receiver.getName())) {
					e.getRecipients().remove(receiver);
					receiver.sendMessage(prefix.replaceAll("Nachricht", msg.replaceAll(receiver.getName(), ChatColor.AQUA + "@" + receiver.getName() + ChatColor.GRAY)));
                    receiver.playSound(receiver.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
				} else {
					e.setFormat(prefix.replaceAll("Nachricht", e.getMessage()));
				}
			}
		}
  	}
}