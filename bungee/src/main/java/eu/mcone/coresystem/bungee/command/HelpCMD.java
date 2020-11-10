/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;

public class HelpCMD extends CoreCommand {

	public HelpCMD(){
		super("help", null, "hilfe", "support");
	}
	
	public void onCommand(CommandSender sender, String[] args){
		BungeeCoreSystem.getInstance().getMessenger().sendSenderSimpleTransl(sender, "system.bungee.command.help");
	}

}
