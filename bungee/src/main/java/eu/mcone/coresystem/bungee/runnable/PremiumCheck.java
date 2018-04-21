/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.runnable;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;

import java.sql.SQLException;
import java.util.UUID;

public class PremiumCheck implements Runnable{
	 
	public void run() {
	    BungeeCoreSystem.getInstance().getMySQL(1).selectAsync("SELECT * FROM bungeesystem_premium", rs -> {
            long millis = System.currentTimeMillis() / 1000;

            try{
                while(rs.next()) {
                    if ((rs.getLong(rs.getString("timestamp")) - millis) < 0) {
                        Messager.console("§7Dem Spieler §f" + BungeeCoreSystem.getInstance().getPlayerUtils().fetchName(UUID.fromString(rs.getString("uuid"))) + " §7wird der Rang §f" + rs.getString("group") + " §7entzogen");
                        BungeeCoreSystem.getInstance().getMySQL(1).update("UPDATE userinfo SET gruppe='" + rs.getString("old_group") + "' WHERE uuid='" + rs.getString("uuid") + "'");

                        BungeeCoreSystem.getInstance().getMySQL(1).select("SELECT gruppe FROM `userinfo` WHERE uuid='" + rs.getString("uuid") + "'", rs_info -> {
                            try {
                                if (rs_info.next()) {
                                    String value = rs_info.getString("gruppe");

                                    if (value.equalsIgnoreCase("Spieler")) {
                                        BungeeCoreSystem.getInstance().getMySQL(1).update("DELETE FROM bungeesystem_premium WHERE uuid = '" + rs.getString("uuid") + "';");
                                    } else {
                                        Messager.console("§7[§cMySQL ERROR§7] §4DER SPIELER KONNTE NICHT GELÖSCHT WERDEN OBWOHL SEIN PREMIUM RANG ABGLAUFEN IST!");
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
    }
	   
}
