/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener{

	@EventHandler
	public void on(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMonitor(PlayerQuitEvent e){
		Bukkit.getScheduler().runTask(CoreSystem.getInstance(), CoreSystem.getCorePlayer(e.getPlayer())::unregister);
	}

}