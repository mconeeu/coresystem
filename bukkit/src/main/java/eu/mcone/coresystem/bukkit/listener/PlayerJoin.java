/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.scoreboard.MainScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener{
	
	@EventHandler
	public void on(PlayerJoinEvent e){
		Player bp = e.getPlayer();
		CorePlayer p = CoreSystem.getCorePlayer(bp);

		e.setJoinMessage(null);
        p.setScoreboard(new MainScoreboard(p));
        CoreSystem.getInstance().getNickManager().setNicks(bp);

		if (CoreSystem.cfg.getConfig().getBoolean("Tablist")){
		    for (CorePlayer cp : CoreSystem.getOnlineCorePlayers()) {
		    	cp.getScoreboard().reload();
			}
		}
	}
}