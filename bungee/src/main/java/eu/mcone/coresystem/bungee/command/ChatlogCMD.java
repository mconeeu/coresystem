/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.listener.ChatListener;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class ChatlogCMD extends CorePlayerCommand {

    public ChatlogCMD() {
        super("chatlog", null, "cl");
    }

    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 1) {
            final ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);
            long millis = System.currentTimeMillis() / 1000;

            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUniqueId()))
                return;

            if (t != null) {
                if (t != p) {
                    if (!t.hasPermission("group.team")) {
                        Set<Map.Entry<Integer, Map<Long, String>>> playerhashmap = ChatListener.playerTreeMap.get(t).descendingMap().entrySet();

                        if (playerhashmap.size() >= 1) {
                            Msg.send(p, "§2Dein Chatlog wird unter §fhttps://www.mcone.eu/chatlog.php?uuid=" + p.getUniqueId() + "&time=" + millis + "§2 erstellt!");
                            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                                Map<Integer, Map<Long, String>> messages = new HashMap<>();

                                int i = 0;
                                for (Map.Entry<Integer, Map<Long, String>> entry : playerhashmap) {
                                    i++;
                                    if (i <= 15) {
                                        messages.put(entry.getKey(), entry.getValue());
                                    } else {
                                        break;
                                    }
                                }

                                BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_chatlog").updateOne(
                                        eq("uuid", p.getUniqueId()),
                                        set("messages", messages),
                                        new UpdateOptions().upsert(true)
                                );
                            });
                        } else {
                            Msg.send(p, "§4Der Spieler muss mindestens eine Nachricht geschrieben haben!");
                        }
                    } else {
                        Msg.send(p, "§4Du darfst keinen Chatlog von Teammitgliedern erstellen!");
                    }
                } else {
                    Msg.send(p, "§4Du kannst keine Chatlog über dich selbst erstellen");
                }
            } else {
                Msg.send(p, "§4Der Spieler §c" + args[0] + " §4ist nicht online!");
            }
        } else {
            Msg.send(p, "§cBitte benutze /chatlog <Spieler>");
        }
    }

}
