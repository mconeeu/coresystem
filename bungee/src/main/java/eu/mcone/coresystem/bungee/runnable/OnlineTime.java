/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class OnlineTime implements Runnable {

	@Override
	public void run() {
		for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			int i = (int) BungeeCoreSystem.getSystem().getMongoDBManager().getObject("uuid", p.getUniqueId(), "onlinetime", "userinfo");
			BungeeCoreSystem.getSystem().getMongoDBManager().updateDocument("uuid", p.getUniqueId(), "onlinetime", i+1, "userinfo");
			//BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE `userinfo` SET onlinetime=onlinetime+1 WHERE `uuid`='" + p.getUniqueId() + "'");
		}
	} 
}
