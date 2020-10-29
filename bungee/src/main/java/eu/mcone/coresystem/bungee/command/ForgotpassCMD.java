/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ForgotpassCMD extends Command {

    public ForgotpassCMD() {
        super("forgotpass", null, "passwortvergessen");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUniqueId()))
                return;

            if (args.length == 0) {
                ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                    String https_url = "https://api.mcone.eu/forgotpass.php?access_token=ahmyC@aSp6F,6MPaF]f.kXpn,h6CBc-&uuid=" + p.getUniqueId().toString();
                    URL url;

                    try {
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§7Bitte warten...");
                        url = new URL(https_url);
                        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String rtn = br.readLine();


                        JsonObject result = new JsonParser().parse(rtn).getAsJsonObject();
                        if (result.get("result").getAsString().equalsIgnoreCase("success")) {
                            BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§2" + result.get("msg").getAsString());
                        } else if (result.get("result").getAsString().equalsIgnoreCase("error")) {
                            BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§4" + result.get("msg").getAsString());
                        } else {
                            BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§4Es ist ein Fehler aufgetreten!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, "§cBitte benutze /forgotpass");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSender(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }

}
