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
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WhereisCMD extends Command{
	
	public WhereisCMD(){
    	 super("Whereis", null, "whois");
     }

	public void execute(final CommandSender sender, final String[] args){
		if(sender instanceof ProxiedPlayer) {
			final ProxiedPlayer p = (ProxiedPlayer) sender;
			if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
			CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

			if (p.hasPermission("system.bungee.whereis")) {
				if (args.length == 1) {
					ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

					if (target == null) {
						Messager.sendSimple(p, new TextComponent(CoreSystem.sqlconfig.getConfigValue("System-No-Online-Player")));
						return;
					}
					Messager.sendSimple(p, new TextComponent("§8§m----------------§r§8| §bWhereis §8§m|----------------"));
					Messager.sendSimple(p, new TextComponent("§7» §3Name§f: " + target.getName()));
					Messager.sendSimple(p, new TextComponent("§7» §3Server§f: " + target.getServer().getInfo().getName()));
					Messager.sendSimple(p, new TextComponent("§7» §3Adresse§f: " + target.getAddress()));
					Messager.sendSimple(p, new TextComponent("§7» §3Verbunden über§f: " + target.getServer().getAddress()));
					Messager.sendSimple(p, new TextComponent("§7» §3UUID§f: " + target.getUniqueId().toString()));
					Messager.sendSimple(p, new TextComponent("§8§m----------------§r§8| §bWhereis §8§m|----------------"));


				} else {
					Messager.send(p, "§cVerwendung: /whereis <Name>");
				}
			} else {
				Messager.send(sender, CoreSystem.sqlconfig.getConfigValue("System-NoPerm"));
			}
		} else {
			Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
		}
    }
}
