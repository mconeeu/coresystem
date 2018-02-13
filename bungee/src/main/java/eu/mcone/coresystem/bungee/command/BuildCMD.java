/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BuildCMD extends Command{

	
	public BuildCMD(){
		super("build", null);
	}
	
	public void execute(final CommandSender sender, final String[] args){
		if(sender instanceof ProxiedPlayer){
			if(sender.hasPermission("system.bungee.build") || sender.hasPermission("system.*")){
				ProxiedPlayer p = (ProxiedPlayer)sender;

				if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
                CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

				ServerInfo Lobby = ProxyServer.getInstance().getServerInfo("Build");
				
				if(p.getServer().getInfo() == Lobby){
                    Messager.send(p, "§4Du befindest dich bereits auf diesem Server!");
				}else{
				p.connect(Lobby);
                    Messager.send(p, "§2Du wirst zum Build-Server verbunden!");
				}
			}else{
                Messager.send(sender, CoreSystem.sqlconfig.getConfigValue("System-NoPerm"));
			}
		} else {
			Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
		}
	}

}
