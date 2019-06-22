/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.labymod.LabyPermission;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.bots.teamspeak.TeamspeakVerifier;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class PostLoginListener implements Listener {

    @EventHandler
    public void on(PostLoginEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        final CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUniqueId());

        BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "\n\n\n\n§8[§7§l!§8] §3MC ONE §8» §7§o" + getRandomWelcomeMSG(p, cp.isNew()) + ", §f§o" + p.getName() + "§7§o!");
        if (cp.isNew()) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(p, "privacy");
            BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "§8[§7§l!§8] §3MC ONE §8» §2Als kleines Willkommensgeschenk bekommst du 20 Coins gutgeschrieben!");
        } else {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getSystem(), () ->
                    BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                            eq("uuid", p.getUniqueId().toString()),
                            combine(
                                    set("ip", cp.getIpAdress()),
                                    set("state", 1),
                                    set("timestamp", System.currentTimeMillis() / 1000)
                            )
                    )
            );
        }

        Map<UUID, String> requests = cp.getFriendData().getRequests();
        if (requests.size() >= 1) {
            BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "");
            BungeeCoreSystem.getInstance().getMessager().send(p, "§7Du hast noch §f" + requests.size() + " §7offene Freundschaftsanfrage(n)!");
            BungeeCoreSystem.getInstance().getMessager().send(p, "§7Benutze §f/friend req §7zum einsehen!");
        }

        Title title = ProxyServer.getInstance().createTitle();
        title.title(new TextComponent("§fWillkommen auf §3§lMC ONE"));
        title.subTitle(new TextComponent("§7§oDein Nummer 1 Minecraftnetzwerk"));
        title.fadeIn(20);
        title.stay(100);
        title.fadeOut(20);

        title.send(p);


        ProxyServer.getInstance().getScheduler().schedule(BungeeCoreSystem.getInstance(), () -> {
            updateTabHeader(p);

            BungeeCoreSystem.getInstance().getLabyModAPI().sendPermissions(p, new HashMap<LabyPermission, Boolean>() {{
                put(LabyPermission.IMPROVED_LAVA, true);
                put(LabyPermission.CROSSHAIR_SYNC, true);
                put(LabyPermission.REFILL_FIX, true);
                put(LabyPermission.GUI_POTION_EFFECTS, false);
                put(LabyPermission.GUI_ARMOR_HUD, false);
                put(LabyPermission.GUI_ITEM_HUD, false);
            }});

            TeamspeakVerifier tsv = BungeeCoreSystem.getSystem().getTeamspeakVerifier();
            if (cp.isTeamspeakIdLinked() && tsv != null) tsv.updateLink(cp, null);
        }, 1000L, TimeUnit.MILLISECONDS);
    }

    private static String getRandomWelcomeMSG(ProxiedPlayer p, boolean isNew) {
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        String part;

        if (hours <= 4) {
            part = "Nacht";
        } else if (hours <= 11) {
            part = "Morgen";
        } else if (hours <= 14) {
            part = "Mittag";
        } else if (hours <= 17) {
            part = "Nachmittag";
        } else if (hours <= 24) {
            part = "Abend";
        } else {
            part = "Tag";
        }

        String[] welcomeSpecial = {"Ey Alde", "Piss dich Alde", "Piss dich du arschgefickte Besenfotze", "Was geht ab du Fotze", "Halt mal dein Maul", "EEEEYYYYYYYY!!!!", "Halt die Fresse jetzt"};
        String[] welcomeMSG = {"Willkommen zurück auf MC ONE", "Schön dich wieder zu sehen", "Was geht ab", "Viel Spaß auf MC ONE", "Servus", "Moin", "Guten " + part};

        if (isNew) {
            return "Willkommen auf MC ONE! Du scheinst neu zu sein";
        } else if (p.getUniqueId().equals(UUID.fromString("9d591956-0d33-4770-907c-66380f67fb29")) || p.getUniqueId().equals(UUID.fromString("504bbc94-4b4c-46f9-8b08-4a21090a49e3"))) {
            return Arrays.asList(welcomeSpecial).get(new Random().nextInt(welcomeSpecial.length));
        } else {
            return Arrays.asList(welcomeMSG).get(new Random().nextInt(welcomeMSG.length));
        }
    }

    public static void updateTabHeader(ProxiedPlayer p) {
        p.setTabHeader(
                new ComponentBuilder(
                        "§3§lMC ONE §8» §7§oDein Nummer 1 Netzwerk"
                                + "\n§7Online: §f§l"+ProxyServer.getInstance().getOnlineCount()+" Spieler§8 ×§7 Server: §f§l" + (p.getServer() != null ? p.getServer().getInfo().getName() : "?")
                                + "\n"
                ).create(),
                new ComponentBuilder(
                        "\n§7Hol' Dir §6§lPremium§7 auf §f§nshop.mcone.eu§7!"
                                + "\n§r  §7TS: §3§nts.mcone.eu§8 ×§7 Discord: §3§ndiscord.mcone.eu§r  "
                ).create()
        );
    }

}
