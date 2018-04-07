/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RestartCMD extends Command{
	
	public RestartCMD(){
		  super("restart", "system.bungee.restart");
		}

	private static ScheduledTask t = null;
	private static int seconds = 300;

	public void execute(final CommandSender sender, final String[] args){
		if (sender.hasPermission("system.bungee.restart")) {
			if (args.length == 0) {
				seconds = 300;
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("cancel")) {
					t.cancel();
					seconds=300;
					t=null;

					Messager.send(sender, "§2Der Neustartvorgang wurde abgebrochen!");
					return;
				} else {
				    int cursor = Integer.valueOf(args[0]);
				    if (cursor >= 5) {
				        seconds = Integer.valueOf(args[0]);
                    } else {
                        Messager.send(sender, "§4Bitte benutze: §c/restart [<Sekunden>]");
                    }
				}
			} else {
				Messager.send(sender, "§4Bitte benutze: §c/restart [<Sekunden>]");
			}

			if (t==null) {
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					Messager.send(p, "§4§oDas Netzwerk wird in " + getTime(seconds) + "§4§o neustarten...");
					Title title = ProxyServer.getInstance().createTitle();
					title.title(new TextComponent("§fWillkommen auf §3§lMC ONE"));
					title.subTitle(new TextComponent("§7§oDein Nummer 1 Minecraftnetzwerk"));
					title.fadeIn(20);
					title.stay(100);
					title.fadeOut(20);

					title.send(p);
				}

				t = ProxyServer.getInstance().getScheduler().schedule(CoreSystem.getInstance(), () -> {
					AtomicInteger i = new AtomicInteger(5);
					ProxyServer.getInstance().getScheduler().schedule(CoreSystem.getInstance(), () -> {
						if (i.get() == 0) ProxyServer.getInstance().stop();

						for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
							Messager.send(p, "§4§oDas Netzwerk startet in §f" + i + "§c Sekunden§4 neu!");
						}
						i.getAndDecrement();
					}, 0, 1, TimeUnit.SECONDS);
				}, seconds - 5, TimeUnit.SECONDS);
			} else {
				Messager.send(sender, "Der Neustartvorgang ist bereits eingeleitet! Benutze §c/restart cancel §4zum abbrechen!");
				Messager.send(sender, "§4§oDas Netzwerk wird in " + getTime(seconds) + "§4§o neustarten...");
			}
		} else {
			Messager.send(sender, CoreSystem.sqlconfig.getConfigValue("System-NoPerm"));
		}
	}

	private static String getTime(int seconds) {
		int minutes = 0;
		StringBuilder sb = new StringBuilder();

		while (seconds > 60) {
			seconds -= 60;
			minutes++;
		}

		if (minutes >= 1) sb.append("§f").append(minutes).append("§c Minute(n)§4§o und ");
		sb.append("§f").append(seconds).append("§c Sekunde(n)");

		return sb.toString();
	}
}
