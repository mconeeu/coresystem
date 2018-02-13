/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener{

	@EventHandler
	public void on(PlayerQuitEvent e){
		e.setQuitMessage(null);
		CoreSystem.mysql1.update("UPDATE userinfo SET status='online' WHERE uuid='" + e.getPlayer().getUniqueId() + "'");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMonitor(PlayerQuitEvent e){
		Player p = e.getPlayer();
		CorePlayer cp = CoreSystem.getCorePlayer(p);

		AFKCheck.players.remove(p.getUniqueId());
		AFKCheck.afkPlayers.remove(p.getUniqueId());

		Bukkit.getScheduler().runTask(CoreSystem.getInstance(), cp::unregister);
	}

}