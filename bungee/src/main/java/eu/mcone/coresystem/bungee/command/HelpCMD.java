/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HelpCMD extends Command{

	public HelpCMD(){
		super("help", null, "hilfe", "support");
	}
	
	public void execute(final CommandSender sender, final String[] args){
		if(sender instanceof ProxiedPlayer){
			final ProxiedPlayer p = (ProxiedPlayer)sender;
			Messager.sendSimple(p, CoreSystem.sqlconfig.getConfigValue("Help-Lines"));
		}else{
			Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
		}
	}
}
