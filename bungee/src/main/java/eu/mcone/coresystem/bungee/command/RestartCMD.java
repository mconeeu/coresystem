/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RestartCMD extends CoreCommand {
	
	public RestartCMD(){
		  super("restart", "system.bungee.restart");
	}

	private static ScheduledTask t = null;

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            restart(sender, 300);
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("cancel")) {
                t.cancel();
                t=null;

                Msg.send(sender, "§2Der Neustartvorgang wurde abgebrochen!");
            } else {
                int cursor = Integer.parseInt(args[0]);

                if (cursor >= 5) {
                    restart(sender, Integer.parseInt(args[0]));
                } else {
                    Msg.sendError(sender, "Der minimale Countdown ist ![5 Sekunden]!");
                }
            }
            return;
        }

        Msg.send(sender, "§4Bitte benutze: §c/restart [<Sekunden>]");
    }

    private void restart(CommandSender sender, int seconds) {
		if (t==null) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				Msg.send(player, "§4§oDas Netzwerk wird in " + getTime(seconds) + "§4§o neustarten...");
				Title title = ProxyServer.getInstance().createTitle();
				title.title(TextComponent.fromLegacyText("§fWillkommen auf §3§lMC ONE"));
				title.subTitle(TextComponent.fromLegacyText("§7§oDein Nummer 1 Minecraftnetzwerk"));
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
						Msg.send(player, "§4§oDas Netzwerk startet in §f" + i + "§c Sekunden§4 neu!");
					}
					i.getAndDecrement();
				}, 0, 1, TimeUnit.SECONDS);
			}, seconds - 5, TimeUnit.SECONDS);
		} else {
			Msg.send(sender, "Der Neustartvorgang ist bereits eingeleitet! Benutze §c/restart cancel §4zum abbrechen!");
			Msg.send(sender, "§4§oDas Netzwerk wird in " + getTime(seconds) + "§4§o neustarten...");
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
