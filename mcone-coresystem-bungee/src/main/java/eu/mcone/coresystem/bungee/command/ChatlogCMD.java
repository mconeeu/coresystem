/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.listener.Chat;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatlogCMD extends Command{

    public ChatlogCMD(){
		    super("chatlog", null, "cl");
		  }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 1) {
                final ProxiedPlayer p = (ProxiedPlayer) sender;
                final ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);
                long millis = System.currentTimeMillis() / 1000;

                if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

                if (t != null) {
                    if (t != p) {
                        if (!t.hasPermission("group.team")) {
                            Set playerhashmap = Chat.playerhashmap.get(t).entrySet();

                            if (playerhashmap.size() >= 1) {
                                Messager.send(p, "§2Dein Chatlog wird unter §fhttps://www.mcone.eu/chatlog.php?uuid=" + p.getUniqueId() + "&time=" + millis + "§2 erstellt!");
                                ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                                    int i = 0;
                                    for (Map.Entry<Integer, HashMap<Long, String>> messages : Chat.playerhashmap.get(t).entrySet()) {
                                        i++;
                                        if (i <= 15) {
                                            Map.Entry<Long, String> msgEntry = messages.getValue().entrySet().iterator().next();

                                            String msg = msgEntry.getValue();
                                            String time = String.valueOf(msgEntry.getKey());

                                            BungeeCoreSystem.getInstance().getMySQL(1).update("INSERT INTO bungeesystem_chatlog (uuid, nachricht, timestamp) VALUES ('" + t.getUniqueId().toString() + "', '" + msg + "', " + time + ")");
                                        } else {
                                            break;
                                        }
                                    }
                                });
                            } else {
                                Messager.send(p, "§4Der Spieler muss mindestens eine Nachricht geschrieben haben!");
                            }
                        } else {
                            Messager.send(p, "§4Du darfst keinen Chatlog von Teammitgliedern erstellen!");
                        }
                    } else {
                        Messager.send(p, "§4Du kannst keine Chatlog über dich selbst erstellen");
                    }
                } else {
                    Messager.send(p, "§4Der Spieler §c" + args[0] + " §4ist nicht online!");
                }
            } else {
                Messager.send(sender, "§cBitte benutze /chatlog <Spieler>");
            }
        } else {
            Messager.sendSimple(sender, BungeeCoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }

}
