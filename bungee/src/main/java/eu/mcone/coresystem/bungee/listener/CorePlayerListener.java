/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.facades.Transl;
import eu.mcone.coresystem.api.bungee.overwatch.punish.Punish;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.command.RegisterCMD;
import eu.mcone.coresystem.bungee.friend.Party;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static com.mongodb.client.model.Filters.eq;

public class CorePlayerListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent e) {
        if (e.getConnection().getVirtualHost().getHostName().equals("register.onegaming.group") || e.getConnection().getVirtualHost().getHostName().equals("register.mcone.eu")) {
            e.setCancelled(true);

            try {
                e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lOneGaming ID" +
                        "\n§7Danke, dass du dir eine OneGaming ID erstellst!" +
                        "\n§r" +
                        "\n§7Dein Register-Code lautet: §c§l" + RegisterCMD.createAndGetNewCode(e.getConnection().getUniqueId()) +
                        "\n§7§oDu kannst ihn nun auf §f§oid.onegaming.group/register/minecraft§7§o eingeben!"
                )));
            } catch (CoreException ingored) {
                e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lOneGaming ID§8 | §7Registrieren" +
                        "\n§r" +
                        "\n§4§oDu hast bereits eine OneGaming ID mit diesem Minecraft Account erstellt!"
                )));
            }
            return;
        }

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

        BungeeCorePlayer p = new BungeeCorePlayer(BungeeCoreSystem.getInstance(), e.getConnection().getAddress().getAddress(), e.getConnection().getUniqueId(), e.getConnection().getName());

        Punish punish = BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getPunish(p.getUuid());
        if (punish != null) {
            if (punish.isBanned()) {
                try {
                    OfflineCorePlayer corePlayer = BungeeCoreSystem.getSystem().getOfflineCorePlayer(punish.getMember());
                    e.setCancelled(true);
                    e.setCancelReason(new TextComponent(TextComponent.fromLegacyText("§f§lMC ONE §3Minecraftnetzwerk"
                            + "\n§7§oDu wurdest vom Netzwerk gebannt"
                            + "\n§r"
                            + "\n§7Gebannt von §8» §e" + corePlayer.getName()
                            + "\n§7Grund §8» §c" + punish.getTemplate().getName() + " §7/§c " + punish.getReason()
                            + "\n§7Gebannt für §8» " + BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getEndeString(punish.getBanEntry().getEnd())
                            + "\n§r"
                            + "\n§2Du hast die Möglichkeit auf einer der folgenden Plattformen einen Entbannungsantrag zu stellen:"
                            + "\n§7TS-Server §8» §fts.mcone.eu"
                            + "\n§7Homepage §8» §fwww.mcone.eu/unban")));
                    p.unregister();
                } catch (PlayerNotResolvedException exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (BungeeCoreSystem.getSystem().getPreferences().get("betaKeySystem", boolean.class)) {
            if (!p.hasPermission("group.team") && BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_betakey").find(eq("uuid", p.getUuid().toString())).first() == null) {
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

        if (BungeeCoreSystem.getSystem().getPreferences().get("maintenance", boolean.class)) {
            if (p.hasPermission("system.bungee.wartung.join")) {
                e.setCancelled(false);
            } else {
                e.setCancelled(true);
                e.setCancelReason(new TextComponent(TextComponent.fromLegacyText(Transl.get("system.bungee.kick.maintenance", p))));
                p.unregister();
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        final BungeeCorePlayer cp = (BungeeCorePlayer) BungeeCoreSystem.getInstance().getCorePlayer(p);

        cp.setState(PlayerState.OFFLINE);

        Party party = Party.getParty(p);
        if (party != null) party.removePlayer(p);

        if (cp.isNicked()) BungeeCoreSystem.getInstance().getNickManager().destroy(p);
        cp.unregister();
    }

}
