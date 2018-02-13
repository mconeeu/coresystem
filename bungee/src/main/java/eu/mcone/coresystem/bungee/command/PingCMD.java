/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PingCMD extends Command{

	public PingCMD(){
		super("ping", null);
	}

	public void execute(final CommandSender sender, final String[] args){
		if(sender instanceof ProxiedPlayer){
			final ProxiedPlayer p = (ProxiedPlayer)sender;
			if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
			CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

			if(args.length == 0){
				Messager.send(sender, "§7Dein Ping: §f" + p.getPing() + "ms");
			}else{
				Messager.send(sender, "§4Bitte benutze: §c/ping");
			}
		}else{
			Messager.console(CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
		}
	}
}
