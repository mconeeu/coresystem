/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.util.Preference;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.ban.BanManager;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.core.mysql.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;

public class PreLogin implements Listener {

	@EventHandler
	public void on(PreLoginEvent e){
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            if (online.getName().equalsIgnoreCase(e.getConnection().getName())) {
                e.setCancelled(true);
                e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk" +
                        "\n§4§oEin Spieler mit deinem Namen befindet sich bereits auf dem Netzwerk" +
                        "\n§r" +
                        "\n§7Dies ist in der Regel der Fall wenn du, oder jemand anderes sich" +
                        "\n§7mit deinem Minecraftaccount in einem anderen Minecraftlauncher" +
                        "\n§7auf dem Netzwerk eingeloggt hat.")));
                return;
            }
        }

		try {
			BungeeCorePlayer p = new BungeeCorePlayer(BungeeCoreSystem.getInstance(), e.getConnection().getAddress().getAddress(), e.getConnection().getName());

            BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM `bungeesystem_bansystem_ban` WHERE end<="+System.currentTimeMillis() / 1000);
			BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT * FROM `bungeesystem_bansystem_ban` WHERE `uuid` = '" + p.getUuid().toString() + "'", rs -> {
				try {
					if (rs.next()) {
						String team_member = rs.getString("team_member");
						String grund = rs.getString("reason");
						String template = rs.getString("template");
						long banTime = rs.getLong("end");

						e.setCancelled(true);
						e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
								+ "\n§7§oDu wurdest vom Netzwerk gebannt"
								+ "\n§r"
								+ "\n§7Gebannt von §8» §e" + team_member
								+ "\n§7Grund §8» §c" + template + " §7/§c " + grund
								+ "\n§7Gebannt für §8» " + BanManager.getEndeString(banTime)
								+ "\n§r"
								+ "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
								+ "\n§7TS-Server §8» §fts.mcone.eu"
								+ "\n§7Homepage §8» §fwww.mcone.eu/unban")));
						p.unregister();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});

			if (BungeeCoreSystem.getSystem().getPreferences().getBoolean(Preference.BETA_KEY_SYSTEM)) {
				BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `timestamp` FROM `bungeesystem_betakey` WHERE `uuid`='" + p.getUuid().toString() + "'", rs -> {
					try {
						if (!p.hasPermission("group.team")) {
							if (!rs.next()) {
								e.setCancelled(true);
								e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
										+ "\n§7§oIst ab jetzt in der Beta-Testphase!"
										+ "\n§r"
										+ "\n§2Wir würden uns freuen, wenn du unser Netzwerk während der Beta testest!"
										+ "\n§7Besuche dazu einfach §f§lmcone.eu/beta §7um dich mit einem Klick freizuschalten!"))
								);
								p.unregister();
							}
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});
			}

			if (BungeeCoreSystem.getSystem().getPreferences().getBoolean(Preference.MAINTENANCE)) {
				if (p.hasPermission("system.bungee.wartung.join")) {
					e.setCancelled(false);
				} else {
					e.setCancelled(true);
					e.setCancelReason(new TextComponent(TextComponent.fromLegacyText(BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.kick.maintenance", Language.GERMAN))));
					p.unregister();
				}
			}
		} catch (PlayerNotResolvedException ex) {
        	e.setCancelled(true);
        	e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
					+ "\n§4§oTut uns leid! Es ist ein Fehler aufgetreten."
					+ "\n§r"
					+ "\n§7Wir konnten deine UUID wegen eines Datenbankfehlers nicht ermitteln!"
					+ "\n§7Bitte melde diesen Fehler einem Teammitglied."
			)));
		}
	}
}
