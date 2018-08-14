/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ProxyPing implements Listener {

    @EventHandler
    public void on(ProxyPingEvent e){
        final PendingConnection con = e.getConnection();
        final ServerPing ping = e.getResponse();
        ServerPing.Players players = ping.getPlayers();
        ServerPing.Protocol version = ping.getVersion();

        /*String hostName = con.getVirtualHost().getHostName();
        if (hostName.equalsIgnoreCase("localhost")) {
            ping.setDescription("§7Hallo, §cI bims§7 1 localhost!");
            return;
        }*/

        if (con.getVersion() < 47) {
            ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                    BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.ping.outdated", Language.GERMAN)
            )));

            version.setProtocol(2);
            version.setName("§4Alte Minecraft-Version!");
            players.getOnline();
            players.getMax();

            ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[] {
                    new ServerPing.PlayerInfo("§3§lMC ONE §7ist seit Dezember 2017 nur noch über", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§7die Minecraft Version §f1.12§7 erreichbar, um dir ein", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§7bestmögliches Spieleerlebnis bieten zu können.", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§6Für ein reibungsloses Spieleerlebnis empfehlen wir", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§6§nLabyMod§6 für die Version 1.12.", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§7§oAlle Infos dazu findest du über den Link §f§nmcone.eu/launcher", UUID.randomUUID())
            };

            players.setSample(sample);
            ping.setPlayers(players);
            ping.setVersion(version);

            return;
        } else if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
            version.setName("§c§oWartungsarbeiten");
            version.setProtocol(2);
            players.getOnline();
            players.getMax();
            ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                    BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.ping.maintenance", Language.GERMAN)
            )));

            ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[] {
                    new ServerPing.PlayerInfo("§3§lMC ONE §7ist gerade nicht verfügbar,", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§7da geplante Wartungsarbeiten durchgeführt werden.", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§r", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("Mehr Infos findest du auf §fstatus.mcone.eu", UUID.randomUUID())
            };

            players.setSample(sample);
            ping.setPlayers(players);
            ping.setVersion(version);

            return;
        }

        ping.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(
                BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.ping", Language.GERMAN)
        )));
        players.getOnline();
        players.getMax();

        ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[] {
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

        players.setSample(sample);
        ping.setPlayers(players);
    }
}
