/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.bungee.CoreSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class OnlineTime implements Runnable {

	@Override
	public void run() {
		for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			CoreSystem.mysql1.update("UPDATE `userinfo` SET onlinetime=onlinetime+1 WHERE `uuid`='" + p.getUniqueId() + "'");
		}
	} 
}
