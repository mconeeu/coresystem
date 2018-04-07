/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnect implements Listener{

	@EventHandler
	public void on(PlayerDisconnectEvent e){
		final ProxiedPlayer p = e.getPlayer();
		final CorePlayer cp = CoreSystem.getCorePlayer(p);

		CoreSystem.mysql1.update("UPDATE userinfo SET status = 'offline' WHERE uuid = '" + p.getUniqueId().toString() + "'");

		Party party = Party.getParty(p);
		if (party != null) party.removePlayer(p);

		if (cp.isNicked()) CoreSystem.getInstance().getNickManager().destroy(p);
		cp.unregister();
	}
}
