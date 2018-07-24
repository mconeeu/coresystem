/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.player.NickManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener{
	
	@EventHandler
	public void on(PlayerJoinEvent e){
		Player bp = e.getPlayer();
		CorePlayer p = BukkitCoreSystem.getInstance().getCorePlayer(bp);

		e.setJoinMessage(null);
        p.setScoreboard(new MainScoreboard());
		((NickManager) BukkitCoreSystem.getInstance().getNickManager()).setNicks(bp);

		for (CorePlayer cp : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
			cp.getScoreboard().reload();
		}
	}

}