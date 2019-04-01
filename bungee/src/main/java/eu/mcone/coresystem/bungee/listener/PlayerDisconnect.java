/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnect implements Listener{

	@EventHandler
	public void on(PlayerDisconnectEvent e){
		final ProxiedPlayer p = e.getPlayer();
		final CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

		((GlobalCorePlayer) cp).setState(PlayerState.OFFLINE);

		Party party = Party.getParty(p);
		if (party != null) party.removePlayer(p);

		if (cp.isNicked()) BungeeCoreSystem.getInstance().getNickManager().destroy(p);
		cp.unregister();
	}
	
}
