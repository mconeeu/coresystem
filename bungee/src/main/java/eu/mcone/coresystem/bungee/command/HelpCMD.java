/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.util.Messager;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class HelpCMD extends Command{

	public HelpCMD(){
		super("help", null, "hilfe", "support");
	}
	
	public void execute(final CommandSender sender, final String[] args){
		Messager.send(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.command.help"));
	}
}
