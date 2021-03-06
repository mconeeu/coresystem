/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.facades.Transl;
import eu.mcone.coresystem.api.core.translation.CoreTranslationManager;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ProxyPingListener implements Listener {

    private static final int MIN_VERSION = 47;

    private static final ServerPing.PlayerInfo[] PLAYER_INFOS_OUTDATED = new ServerPing.PlayerInfo[]{
            new ServerPing.PlayerInfo("§3§lMC ONE §7ist momentan nur über die Minecraft", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7Version §f1.8.X§7 erreichbar, um die bekannteste", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7Minecraftversion mit dem beliebtesten PvP-System", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7zu unterstützen.", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§6Für ein reibungsloses Spieleerlebnis empfehlen wir", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§6§nLabyMod§6 für die Version 1.8.", UUID.randomUUID()),
    };

    private static final ServerPing.PlayerInfo[] PLAYER_INFOS_MAINTENANCE = new ServerPing.PlayerInfo[] {
            new ServerPing.PlayerInfo("§3§lMC ONE §7ist gerade nicht verfügbar,", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7da geplante Wartungsarbeiten durchgeführt werden.", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
            new ServerPing.PlayerInfo("Mehr Infos findest du auf §fstatus.mcone.eu", UUID.randomUUID())
    };

    private static final ServerPing.PlayerInfo[] PLAYER_INFOS = new ServerPing.PlayerInfo[] {
            new ServerPing.PlayerInfo("§3§lMC ONE", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7§oDein Nummer 1 Minecraftnetzwerk", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7Homepage & Bewerbungen: §f§owww.mcone.eu", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7TeamSpeak Server: §f§ots.mcone.eu", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§7Discord Weblink: §f§odiscord.mcone.eu", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§c§lYouTube §8» §f§omcone.eu/yt", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§b§lTwitter §8» §f§o@mconeeu", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§9§lFacebook §8» §f§o@mconeeu", UUID.randomUUID()),
            new ServerPing.PlayerInfo("§r", UUID.randomUUID())
    };

    @EventHandler
    public void on(ProxyPingEvent e){
        final PendingConnection con = e.getConnection();
        final ServerPing ping = e.getResponse();
        ServerPing.Players players = ping.getPlayers();
        ServerPing.Protocol version = ping.getVersion();

        players.getOnline();
        players.getMax();

        if (con.getVersion() < MIN_VERSION) {
            ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                    Transl.get("system.bungee.ping.outdated", CoreTranslationManager.DEFAULT_LANGUAGE)
            )));

            version.setProtocol(2);
            version.setName("§4Alte Minecraft-Version!");

            players.setSample(PLAYER_INFOS_OUTDATED);
        }else if (!con.isOnlineMode()) {
            version.setName("§c§oDu benutzt einen Offline Account");
            version.setProtocol(2);

            ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                    Transl.get("system.bungee.ping.cracked", CoreTranslationManager.DEFAULT_LANGUAGE)
            )));

            players.setSample(PLAYER_INFOS_MAINTENANCE);
        } else if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
            version.setName("§c§oWartungsarbeiten");
            version.setProtocol(2);

            ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                    Transl.get("system.bungee.ping.maintenance", CoreTranslationManager.DEFAULT_LANGUAGE)
            )));

            players.setSample(PLAYER_INFOS_MAINTENANCE);
        } else {
            ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                    Transl.get("system.bungee.ping", CoreTranslationManager.DEFAULT_LANGUAGE)
            )));

            players.setSample(PLAYER_INFOS);
        }

        InetSocketAddress virtualHost = con.getVirtualHost();
        if (virtualHost != null) {
            String hostName = virtualHost.getHostName();

            if (hostName != null && !hostName.endsWith("mcone.eu")) {
                version.setName("§4Unsichere Adresse!");
                version.setProtocol(2);

                players.setSample(new ServerPing.PlayerInfo[] {
                        new ServerPing.PlayerInfo("§c§oDu hast MC ONE mit einer unsicheren Adresse eingespeichert!", UUID.randomUUID()),
                        new ServerPing.PlayerInfo("§c§oBitte benutze §c§nmcone.eu", UUID.randomUUID()),
                        new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
                        new ServerPing.PlayerInfo("§c§oDeine Aktuelle Adresse: §4§o"+hostName, UUID.randomUUID())
                });
            }
        }

        ping.setPlayers(players);
        ping.setVersion(version);
    }
}
