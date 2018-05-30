/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.cloud.api.plugin.CloudAPI;
import eu.mcone.cloud.api.plugin.bungee.BungeeCloudPlugin;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

public class ServerConnect implements Listener {

    @EventHandler
    public void on(ServerConnectEvent e) {
        ServerInfo target = e.getTarget();
        ProxiedPlayer p = e.getPlayer();

        if (p.getServer() == null || !p.getServer().getInfo().equals(target) && BungeeCoreSystem.getSystem().isCloudsystemAvailable()) {
            if (!target.getName().contains("Lobby")) {
                //Ping target server
                target.ping((result, error) -> {
                    if (error != null || result == null) {
                        BungeeCoreSystem.getInstance().sendConsoleMessage("§4Could not ping server " + target.getName() + ": ");
                        if (error != null) error.printStackTrace();
                        return;
                    }

                    //Save data
                    int max = result.getPlayers().getMax();
                    int current = result.getPlayers().getOnline();
                    int free = max - current;
                    ArrayList<ProxiedPlayer> kickable = new ArrayList<>();

                    //Get kickable players
                    for (ProxiedPlayer player : target.getPlayers()) {
                        if ((player.hasPermission("group.spieler") || player.hasPermission("group.spielverderber")) && !player.hasPermission("group.team")) {
                            kickable.add(player);
                        }
                    }

                    //If target eu.mcone.coresystem.api.core.player is in Party
                    if (Party.isInParty(p)) {
                        Party party = Party.getParty(p);

                        assert party != null;
                        int partyMember = party.getMember().size();

                        //If target eu.mcone.coresystem.api.core.player is the Party leader
                        if (party.getLeader().equals(p)) {
                            if (current + partyMember > max) {
                                //If target eu.mcone.coresystem.api.core.player is allowed to kick other
                                if (p.hasPermission("mcone.premium")) {
                                    //If party member count is greater then the server slots
                                    if (partyMember > (kickable.size() + free)) {
                                        e.setCancelled(true);
                                        party.sendAll("§4Der Server §c" + target.getName() + "§4 hat nicht genügend Platz für alle Partyteilnehmer");
                                        return;
                                        //If someone has to be kicked
                                    } else if (partyMember > free) {
                                        int toKick = partyMember - free;

                                        for (ProxiedPlayer kick : kickable) {
                                            if (toKick == 0) break;
                                            kick.connect(((BungeeCloudPlugin) CloudAPI.getInstance().getPlugin()).getFallbackServer());
                                            BungeeCoreSystem.getInstance().getMessager().send(kick, "§4Du wurdest aus der Runde gekickt, da ein höher rangiger Spieler den Server betreten hat!" +
                                                    "\n§7Hol' dir den §6Premium Rang §7um nicht mehr gekickt zu werden! Benutze §f/premium §7 für mehr Infos!");
                                            toKick--;
                                        }
                                    }
                                } else {
                                    if (partyMember > free) {
                                        e.setCancelled(true);
                                        party.sendAll("§4Der Server §c" + target.getName() + "§4 hat nicht genügend Platz für alle Partyteilnehmer!" +
                                                "\n§7Hol' dir den §6Premium Rang §7um um andere aus der Runde zu kicken! Benutze §f/premium §7 für mehr Infos!");
                                        return;
                                    }
                                }
                            }

                            //For all Party members
                            for (ProxiedPlayer m : party.getMember()) {
                                if (target.getName().equals(m.getServer().getInfo().getName()))
                                    continue;

                                //Send info message
                                BungeeCoreSystem.getInstance().getMessager().sendParty(m, "§2Die Party betritt den Server §f" + target.getName());
                                if (m == p) continue;

                                //Send member to server
                                m.connect(target);
                            }

                        }
                    } else {
                        if (current + 1 > max) {
                            //If target eu.mcone.coresystem.api.core.player is allowed to kick other
                            if (p.hasPermission("mcone.premium")) {
                                //If is someone suitable for kick
                                if ((kickable.size() + free) < 1) {
                                    e.setCancelled(true);
                                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Der Server §c" + target.getName() + "§4 ist bereits voll!");
                                    return;
                                }

                                //Kick one eu.mcone.coresystem.api.core.player
                                int i = 1;
                                for (ProxiedPlayer kick : kickable) {
                                    if (i < 1) break;
                                    kick.connect(((BungeeCloudPlugin) CloudAPI.getInstance().getPlugin()).getFallbackServer());
                                    BungeeCoreSystem.getInstance().getMessager().send(kick, "§4Du wurdest aus der Runde gekickt, da ein höher rangiger Spieler den Server betreten hat!" +
                                            "\n§7Hol' dir den §6Premium Rang §7um nicht mehr gekickt zu werden! Benutze §f/premium §7 für mehr Infos!");
                                    i--;
                                }
                            } else {
                                e.setCancelled(true);
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Der Server §c" + target.getName() + "§4 ist bereits voll!" +
                                        "\n§7Hol' dir den §6Premium Rang §7um um andere aus der Runde zu kicken! Benutze §f/premium §7 für mehr Infos!");
                            }
                        }
                    }
                });
            }
        }
    }
    
}
