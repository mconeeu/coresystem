/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.listener.PostLoginListener;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PremiumCheck implements Runnable {

    public void run() {
        for (Document premiumEntry : BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").find()) {
            long millis = System.currentTimeMillis() / 1000;

            if (premiumEntry.getLong("timestamp") - millis < 0) {
                try {
                    OfflineCorePlayer p = BungeeCoreSystem.getInstance().getOfflineCorePlayer(UUID.fromString(premiumEntry.getString("uuid")));
                    BungeeCoreSystem.getInstance().sendConsoleMessage("§7Dem Spieler §f" + BungeeCoreSystem.getInstance().getPlayerUtils().fetchName(UUID.fromString(premiumEntry.getString("uuid"))) + " §7wird der Rang §f" + premiumEntry.getString("group") + " §7entzogen");

                    p.removeGroup(Group.getGroupById(premiumEntry.getInteger("group")));
                    if (p instanceof BungeeCorePlayer)
                        ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Type.GROUP_CHANGE, (BungeeCorePlayer) p, p.getGroups()));
                } catch (PlayerNotResolvedException e) {
                    e.printStackTrace();
                }

                for (Document infoDocument : BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("").find(eq("uuid", premiumEntry.getString("uuid")))) {
                    if (infoDocument.getString("grupper").equalsIgnoreCase("Spieler")) {
                        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_premium").deleteOne(eq("uuid", infoDocument.getString("uuid")));
                    } else {
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§7[§cMySQL ERROR§7] §4DER SPIELER KONNTE NICHT GELÖSCHT WERDEN OBWOHL SEIN PREMIUM RANG ABGLAUFEN IST!");
                    }
                }
            }
        }

        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            PostLoginListener.updateTabHeader(p);
        }
    }
}
