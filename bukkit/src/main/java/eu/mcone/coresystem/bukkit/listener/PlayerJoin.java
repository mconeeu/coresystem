/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.scoreboard.MainScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener{
	
	@EventHandler
	public void on(PlayerJoinEvent e){
		Player bp = e.getPlayer();
		BukkitCorePlayer p = BukkitCoreSystem.getInstance().getCorePlayer(bp);

		e.setJoinMessage(null);
        p.setScoreboard(new MainScoreboard());
        BukkitCoreSystem.getInstance().getNickManager().setNicks(bp);

		if (BukkitCoreSystem.getSystem().getYamlConfig().getConfig().getBoolean("Tablist")){
		    for (BukkitCorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
		    	cp.getScoreboard().reload(BukkitCoreSystem.getInstance());
			}
		}
	}

}