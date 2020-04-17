/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RestartCMD extends Command {
	
	public RestartCMD(){
		  super("restart", "system.bungee.restart");
	}

	private static ScheduledTask t = null;

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            restart(sender, 300);
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("cancel")) {
                t.cancel();
                t=null;

                BungeeCoreSystem.getInstance().getMessenger().send(sender, "§2Der Neustartvorgang wurde abgebrochen!");
            } else {
                int cursor = Integer.valueOf(args[0]);

                if (cursor >= 5) {
                    restart(sender, Integer.valueOf(args[0]));
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Bitte benutze: §c/restart [<Sekunden>]");
                }
            }
            return;
        }

        BungeeCoreSystem.getInstance().getMessenger().send(sender, "§4Bitte benutze: §c/restart [<Sekunden>]");
    }

    private void restart(CommandSender p, int seconds) {
		if (t==null) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				BungeeCoreSystem.getInstance().getMessenger().send(player, "§4§oDas Netzwerk wird in " + getTime(seconds) + "§4§o neustarten...");
				Title title = ProxyServer.getInstance().createTitle();
				title.title(new TextComponent("§fWillkommen auf §3§lMC ONE"));
				title.subTitle(new TextComponent("§7§oDein Nummer 1 Minecraftnetzwerk"));
				title.fadeIn(20);
				title.stay(100);
				title.fadeOut(20);

				title.send(player);
			}

			t = ProxyServer.getInstance().getScheduler().schedule(BungeeCoreSystem.getInstance(), () -> {
				AtomicInteger i = new AtomicInteger(5);
				ProxyServer.getInstance().getScheduler().schedule(BungeeCoreSystem.getInstance(), () -> {
					if (i.get() == 0) ProxyServer.getInstance().stop();

					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						BungeeCoreSystem.getInstance().getMessenger().send(player, "§4§oDas Netzwerk startet in §f" + i + "§c Sekunden§4 neu!");
					}
					i.getAndDecrement();
				}, 0, 1, TimeUnit.SECONDS);
			}, seconds - 5, TimeUnit.SECONDS);
		} else {
			BungeeCoreSystem.getInstance().getMessenger().send(p, "Der Neustartvorgang ist bereits eingeleitet! Benutze §c/restart cancel §4zum abbrechen!");
			BungeeCoreSystem.getInstance().getMessenger().send(p, "§4§oDas Netzwerk wird in " + getTime(seconds) + "§4§o neustarten...");
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
