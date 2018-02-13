/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.gson.Gson;
import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ForgotpassCMD extends Command{

	public ForgotpassCMD(){
	super("forgotpass", null, "passwortvergessen");
	}

	public void execute(final CommandSender sender, final String[] args) {
		if (sender instanceof ProxiedPlayer) {
			final ProxiedPlayer p = (ProxiedPlayer) sender;
			if (!CoreSystem.getInstance().getCooldownSystem().canExecute(this.getClass(), p)) return;
			CoreSystem.getInstance().getCooldownSystem().addPlayer(p.getUniqueId(), this.getClass());

			if (args.length == 0) {
				ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
					String https_url = "https://api.mcone.eu/forgotpass.php?access_token=ahmyC@aSp6F,6MPaF]f.kXpn,h6CBc-&uuid=" + p.getUniqueId().toString();
					URL url;

					try {
						Messager.send(p, "§7Bitte warten...");
						url = new URL(https_url);
						HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
						BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
						String rtn = br.readLine();

						result rs = new Gson().fromJson(rtn, result.class);
						//System.out.println("Msg: " + rs.msg);
						if (rs.result.equalsIgnoreCase("success")) {
							Messager.send(sender, "§2" + rs.msg);
						} else if (rs.result.equalsIgnoreCase("error")) {
							Messager.send(sender, "§4" + rs.msg);
						} else {
							Messager.send(sender, "§4Es ist ein Fehler aufgetreten!");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} else {
				Messager.send(sender, "§cBitte benutze /forgotpass");
			}
		} else {
			Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
		}
	}

	public class result {
	  String result;
	  String msg;
	}
}
