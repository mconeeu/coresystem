/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class OnlineTime implements Runnable {

	@Override
	public void run() {
		for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid",  p.getUniqueId().toString()), inc("online_time", 1));
			//BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE `userinfo` SET onlinetime=onlinetime+1 WHERE `uuid`='" + p.getUniqueId() + "'");
		}
	} 
}
