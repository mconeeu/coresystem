/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.lib.labymod.AntiLabyMod;
import eu.mcone.coresystem.lib.labymod.LabyPermission;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PostLogin implements Listener{

    private static boolean isNew = false;

    @EventHandler
    public void on(PostLoginEvent e){
        final ProxiedPlayer p = e.getPlayer();
        final CorePlayer cp = CoreSystem.getCorePlayer(p.getUniqueId());

        final InetSocketAddress IPAdressPlayer = p.getAddress();
        String sfullip = IPAdressPlayer.toString();
        String[] fullip;
        String[] ipandport;
        fullip = sfullip.split("/");
        String sIpandPort = fullip[1];
        ipandport = sIpandPort.split(":");
        String ip = ipandport[0];
        final long millis = System.currentTimeMillis();

        CoreSystem.mysql1.select("SELECT `uuid` FROM `userinfo` WHERE `uuid` = '" + p.getUniqueId().toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    CoreSystem.mysql1.update("UPDATE `userinfo` SET `name` = '" + p.getName() + "', `ip` = '" + ip + "' , status = 'online', `timestamp` = '" + millis / 1000 + "' WHERE `uuid`='" + p.getUniqueId().toString() + "';");
                } else {
                    isNew = true;
                    CoreSystem.mysql1.update("INSERT INTO `userinfo` (`uuid`, `name`, `groups`, `coins`, `status`, `ip`, `timestamp`, `onlinetime`) VALUES ('" +  p.getUniqueId().toString() + "', '" +  p.getName() + "', '[11]', 20, 'online', '" + ip + "', '" +  millis / 1000 + "' , 0)");
                }
            }catch (SQLException e1){
                e1.printStackTrace();
            }
        });

        Messager.sendSimple(p, "\n\n§8[§7§l!§8] §3MC ONE §8» §7§o" + getRandomWelcomeMSG(p) + ", §f§o" + p.getName() + "§7§o!");
        if (isNew) {
            Messager.sendSimple(p, "§8[§7§l!§8] §3MC ONE §8» §2Als kleines Willkommensgeschenk bekommst du 20 Coins gutgeschrieben!");
        }

        if(p.hasPermission("system.bungee.report")) {
            CoreSystem.mysql1.select("SELECT `id`, `title` FROM `website_ticket` WHERE `cat`='Spielerreport' AND `state`='pending';", rs -> {
                try {
                    int desc = 0;
                    while (rs.next()) {
                        desc++;
                    }

                    if (desc > 0) {
                        if (desc <= 10) {
                            Messager.send(p, "§4§oFolgende Reports sind noch unbearbeitet!");
                            rs.beforeFirst();
                            while (rs.next()) {
                                Messager.sendSimple(p, "§7» " + rs.getInt("id") + ". §f" + rs.getString("title"));
                            }
                            Messager.sendSimple(p, "");
                        } else {
                            Messager.send(p, "§7Es sind noch §c" + desc + " §7Reports offen");
                        }
                    } else {
                        Messager.send(p, "§2Es sind alle Reports erledigt!");
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
        }

        Map<UUID, String> requests = cp.getRequests();
        if (requests.size() >= 1) {
            Messager.sendSimple(p, "");
            Messager.send(p, "§7Du hast noch §f"+requests.size()+" §7offene Freundschaftsanfrage(n)!");
            Messager.send(p, "§7Benutze §f/friend req §7zum einsehen!");
        }

        Title title = ProxyServer.getInstance().createTitle();
        title.title(new TextComponent("§fWillkommen auf §3§lMC ONE"));
        title.subTitle(new TextComponent("§7§oDein Nummer 1 Minecraftnetzwerk"));
        title.fadeIn(20);
        title.stay(100);
        title.fadeOut(20);

        title.send(p);


        isNew = false;

        ProxyServer.getInstance().getScheduler().schedule(CoreSystem.getInstance(), () -> {
            /*if (p.getServer() != null) {
                p.setTabHeader(
                        new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "§f§lMC ONE §3Minecraftnetzwerk §8» §7" + e.getPlayer().getServer().getInfo().getName())).create(),
                        new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "§7§oPublic Beta 5.0")).create()
                );
            }*/

            new AntiLabyMod()
                    .set(LabyPermission.IMPROVED_LAVA, true)
                    .set(LabyPermission.CROSSHAIR_SYNC, true)
                    .set(LabyPermission.REFILL_FIX, true)
                    .set(LabyPermission.GUI_POTION_EFFECTS, false)
                    .set(LabyPermission.GUI_ARMOR_HUD, false)
                    .set(LabyPermission.GUI_ITEM_HUD, false)
                    .send((channel, bytes) -> p.unsafe().sendPacket(new PluginMessage(channel, bytes, false))
            );
        },1000L, TimeUnit.MILLISECONDS);
    }

    private static String getRandomWelcomeMSG(ProxiedPlayer p) {
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        String part;

        if (hours<=4) {
            part = "Nacht";
        } else if (hours<=11) {
            part = "Morgen";
        } else if (hours<=14) {
            part = "Mittag";
        } else if (hours<=17) {
            part = "Nachmittag";
        } else if (hours<=24) {
            part = "Abend";
        } else {
            part = "Tag";
        }

        String[] welcomeSpecial = {"Ey Alde", "Piss dich Alde", "Piss dich du arschgefickte Besenfotze", "Was geht ab du Fotze", "Halt mal dein Maul", "EEEEYYYYYYYY!!!!", "Halt die Fresse jetzt"};
        String[] welcomeMSG = {"Willkommen zurück auf MC ONE", "Schön dich wieder zu sehen", "Was geht ab", "Viel Spaß auf MC ONE", "Servus", "Moin", "Guten "+part};

        if (isNew) {
            return "Willkommen auf MC ONE! Du scheinst neu zu sein";
        } else if (p.getUniqueId().equals(UUID.fromString("9d591956-0d33-4770-907c-66380f67fb29")) || p.getUniqueId().equals(UUID.fromString("504bbc94-4b4c-46f9-8b08-4a21090a49e3"))) {
            return Arrays.asList(welcomeSpecial).get(new Random().nextInt(welcomeSpecial.length));
        } else {
            return Arrays.asList(welcomeMSG).get(new Random().nextInt(welcomeMSG.length));
        }
    }
}
