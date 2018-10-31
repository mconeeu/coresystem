/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.ban.BanManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

public class Chat implements Listener {

    final private static ArrayList<String> cmds = new ArrayList<>(Arrays.asList("/greload", "/glist", "/alertraw", "/end", "/alert", "/alertraw", "/me", "/send", "/plugins", "/pl", "/demote", "/promote", "/permissionsex:", "/pex", "/pl", "/?", "/bungee", "/me", "/bukkit:plugins", "/bukkit:pl", "/bukkit:?", "/bukkit:help", "/bukkit:me", "/bukkit:mE", "/", "/version", "/tell", "/me", "/minecraft:me"));
    final private static ArrayList<String> forbiddenWords = new ArrayList<>(Arrays.asList("nazi", "Nazi", "Hitler", "HURENSOHN!", "pedo", "köter", "scheiss", "scheiß", "huan", "opfer", "opfa", "ez", "fotze", "l2p", "Anal", "Sex", "fick", "Fick", "Vagina", "Penis", "Arsch", "Kanacke", "ez", "e2", "easy", "eazy", "noob", "n00b", "nab", "nob", "n00b", "n0b", "bastard", "bastard", "Pimmel", "Misset", "Misstgeburt", "Missgeburt", "Popo", "Po", "Penetrant", "porno", "schlampen", "nutten", "transen", "blowjob", "sexy", "kotzen", "kaka", "orin", "scheiße", "kot", "kacken", "fettsack", "affenkind", "Hurensohn", "Du Zigeuner", "Du Tonne", "Du Müllsack", "Du Mülltonne", "Mutterficker", "spasst", "spast", "motherfucker", "Hoe", "Bitch", "Slut", "Bl�dmann", "Pussy", "Pu*sy", "Dick", "Porno", "ddos", "dos", "nippel", "lappen", "lapen", "huso", "l4ppen", "Missthaufen", "nippelsauger", "Plauge", "Knackfuck", "Knackfuss", "cock", "sandler", "sandla", "fuppa"));

    public static Map<ProxiedPlayer, TreeMap<Integer, Map<Long, String>>> playerTreeMap = new HashMap<>();

    @EventHandler(priority = 64)
    public void on(ChatEvent e) {
        final long millis = System.currentTimeMillis() / 1000;
        final String msg = e.getMessage();
        final ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        final CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p.getUniqueId());

        if (p.hasPermission("system.bungee.cmd.bypass")) {
            return;
        } else if (!(e.getSender() instanceof ProxiedPlayer)) {
            return;
        } else if (e.getMessage().startsWith("/")) {
            if (new ArrayList<>(Arrays.asList("/plugins", "/pl", "/bukkit:pl", "bukkit:pl")).contains(msg)) {
                BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "§fPlugins (6): §aDa§f, §amusst§f, §adu§f, §afrüher§f, §aaufstehen§f, §a" + p.getName());
                e.setCancelled(true);
                return;
            } else if (cmds.contains(msg)) {
                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du hast keine Berechtigung für den Befehl §c" + msg + "§4!");
                e.setCancelled(true);
                return;
            } else {
                e.setCancelled(false);
            }
        } else {
            if (cp.isMuted()) {
                e.setCancelled(true);
                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du bist noch für " + BanManager.getEndeString(cp.getMuteTime()) + "§4 gemutet!");
                return;
            }

            boolean canelled = false;

            for (Iterator<String> i = forbiddenWords.iterator(); i.hasNext(); ) {
                String forbiddenWord = i.next();
                if (msg.contains(forbiddenWord)) canelled = true;
            }

            if (canelled) {
                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte achte auf deine Ausdrucksweise!");
                e.setCancelled(true);
                return;
            } else {
                e.setCancelled(false);
            }
        }

        if (playerTreeMap.containsKey(p)) {
            Map<Long, String> news = new HashMap<>();
            news.put(millis, e.getMessage());
            playerTreeMap.get(p).put(playerTreeMap.get(p).size() + 1, news);
        } else {
            Map<Long, String> news = new HashMap<>();
            news.put(millis, e.getMessage());

            TreeMap<Integer, Map<Long, String>> news2 = new TreeMap<>();
            news2.put(1, news);

            playerTreeMap.put(p, news2);
        }
    }
}
