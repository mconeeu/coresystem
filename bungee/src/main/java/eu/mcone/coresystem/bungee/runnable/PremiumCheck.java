/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import org.bson.Document;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class PremiumCheck implements Runnable {

    public void run() {

        for (Document premiumDocument : BungeeCoreSystem.getSystem().getMongoDatabase().getDocumentsInCollection("bungeesystem_premium")) {
            long millis = System.currentTimeMillis() / 1000;

            if (premiumDocument.getLong("timestamp") - millis < 0) {
                BungeeCoreSystem.getInstance().sendConsoleMessage("§7Dem Spieler §f" + BungeeCoreSystem.getInstance().getPlayerUtils().fetchName(UUID.fromString(premiumDocument.getString("uuid"))) + " §7wird der Rang §f" + premiumDocument.getString("group") + " §7entzogen");
                BungeeCoreSystem.getSystem().getMongoDatabase().getCollection("userinfo").updateOne(eq("uuid", premiumDocument.getString("uuid")), combine(set("group", premiumDocument.getString("odl_group"))));

                for (Document infoDocument : BungeeCoreSystem.getSystem().getMongoDatabase().getCollection("").find(eq("uuid", premiumDocument.getString("uuid")))) {
                    if (infoDocument.getString("grupper").equalsIgnoreCase("Spieler")) {
                        BungeeCoreSystem.getSystem().getMongoDatabase().getCollection("bungeesystem_premium").deleteOne(eq("uuid", infoDocument.getString("uuid")));
                    } else {
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§7[§cMySQL ERROR§7] §4DER SPIELER KONNTE NICHT GELÖSCHT WERDEN OBWOHL SEIN PREMIUM RANG ABGLAUFEN IST!");
                    }
                }
            }
        }

        /*
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).selectAsync("SELECT * FROM bungeesystem_premium", rs -> {
            long millis = System.currentTimeMillis() / 1000;

            try{
                while(rs.next()) {
                    if ((rs.getLong(rs.getString("timestamp")) - millis) < 0) {
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§7Dem Spieler §f" + BungeeCoreSystem.getInstance().getPlayerUtils().fetchName(UUID.fromString(rs.getString("uuid"))) + " §7wird der Rang §f" + rs.getString("group") + " §7entzogen");
                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET gruppe='" + rs.getString("old_group") + "' WHERE uuid='" + rs.getString("uuid") + "'");

                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT gruppe FROM `userinfo` WHERE uuid='" + rs.getString("uuid") + "'", rs_info -> {
                            try {
                                if (rs_info.next()) {
                                    String value = rs_info.getString("gruppe");

                                    if (value.equalsIgnoreCase("Spieler")) {
                                        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM bungeesystem_premium WHERE uuid = '" + rs.getString("uuid") + "';");
                                    } else {
                                        BungeeCoreSystem.getInstance().sendConsoleMessage("§7[§cMySQL ERROR§7] §4DER SPIELER KONNTE NICHT GELÖSCHT WERDEN OBWOHL SEIN PREMIUM RANG ABGLAUFEN IST!");
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        */
    }
}
