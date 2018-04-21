/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LobbyCMD extends Command{
	
	public LobbyCMD(){
		super("lobby", null, "l", "hub", "h");
	}

	public void execute(final CommandSender sender, final String[] args){
		if(sender instanceof ProxiedPlayer){
			final ProxiedPlayer p = (ProxiedPlayer)sender;
			if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

			final ServerInfo Lobby = ProxyServer.getInstance().getServerInfo(BungeeCoreSystem.sqlconfig.getConfigValue("System-Server-Lobby"));
			
			if(p.getServer().getInfo() == Lobby){
				Messager.send(p, BungeeCoreSystem.sqlconfig.getConfigValue("System-Already-Lobby"));
			}else{
			p.connect(Lobby);
				Messager.send(p, BungeeCoreSystem.sqlconfig.getConfigValue("System-Connect-Lobby"));
			}
		} else {
			Messager.sendSimple(sender, BungeeCoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
		}
	}

}
