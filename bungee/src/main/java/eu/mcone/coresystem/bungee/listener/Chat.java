/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.ban.BanManager;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

public class Chat implements Listener {

    final private static ArrayList<String> cmds = new ArrayList<>(Arrays.asList("/greload", "/glist", "/alertraw", "/end", "/alert", "/alertraw", "/me", "/send", "/plugins", "/pl", "/demote", "/promote", "/permissionsex:", "/pex", "/pl", "/?", "/bungee", "/me", "/bukkit:plugins", "/bukkit:pl", "/bukkit:?", "/bukkit:help", "/bukkit:me", "/bukkit:mE", "/", "/version", "/tell", "/me", "/minecraft:me"));
    final private static ArrayList<String> forbiddenWords = new ArrayList<>(Arrays.asList("nazi", "Nazi", "Hitler", "HURENSOHN!", "pedo", "köter", "scheiss", "scheiß", "huan", "opfer", "opfa", "ez", "fotze", "l2p", "Anal", "Sex", "fick", "Fick", "Vagina", "Penis", "Arsch", "Kanacke", "ez", "eZ", "easy", "eazy", "noob", "nab", "nob", "n00b", "n0b", "bastard", "bastard", "Pimmel", "Misset", "Misstgeburt", "Missgeburt", "Popo", "Po", "Penetrant", "porno", "schlampen", "nutten", "transen", "blowjob", "sexy", "kotzen", "kaka", "orin", "scheiße", "kot", "kacken", "fettsack", "affenkind", "Hurensohn", "Du Zigeuner", "Du Tonne", "Du Müllsack", "Du Mülltonne", "Mutterficker", "spasst", "spast", "motherfucker", "Hoe", "Bitch", "Slut", "Bl�dmann", "Pussy", "Pu*sy", "Dick", "Porno", "ddos", "dos", "nippel", "lappen", "lapen", "huso", "l4ppen", "Missthaufen", "nippelsauger", "Plauge", "Knackfuck", "Knackfuss", "cock", "sandler", "sandla", "fuppa"));

    public static Map<ProxiedPlayer, HashMap<Integer, HashMap<Long, String>>> playerhashmap = new HashMap<>();

    @EventHandler(priority = 64)
    public void on(ChatEvent e) {
        final long millis = System.currentTimeMillis() / 1000;
        final String msg = e.getMessage();
        final ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        final CorePlayer cp = CoreSystem.getCorePlayer(p.getUniqueId());

        if (p.hasPermission("system.bungee.cmd.bypass")) {
            return;
        } else if (!(e.getSender() instanceof ProxiedPlayer)) {
            return;
        } else if (e.getMessage().startsWith("/")) {
            if (new ArrayList<>(Arrays.asList("/plugins", "/pl", "/bukkit:pl", "bukkit:pl")).contains(msg)) {
                Messager.sendSimple(p, "§fPlugins (6): §aDa§f, §amusst§f, §adu§f, §afrüher§f, §aaufstehen§f, §a" + p.getName());
                e.setCancelled(true);
                return;
            } else if (cmds.contains(msg)) {
                Messager.send(p, "§4Du hast keine Berechtigung für den Befehl §c" + msg + "§4!");
                e.setCancelled(true);
                return;
            } else {
                e.setCancelled(false);
            }
        } else {
            if (cp.isMuted()) {
                e.setCancelled(true);
                Messager.send(p, "§4Du bist noch für " + BanManager.getEndeString(cp.getMutetime()) + "§4 gemutet!");
                return;
            }

            boolean canelled = false;

            for (Iterator<String> i = forbiddenWords.iterator(); i.hasNext(); ) {
                String forbiddenWord = i.next();
                if (msg.contains(forbiddenWord)) canelled = true;
            }

            if (canelled) {
                Messager.send(p, "§4Bitte achte auf deine Ausdrucksweise!");
                e.setCancelled(true);
                return;
            } else {
                e.setCancelled(false);
            }
        }

        if (playerhashmap.containsKey(p)) {
            HashMap<Long, String> news = new HashMap<>();
            news.put(millis, e.getMessage());
            playerhashmap.get(p).put(playerhashmap.get(p).size() + 1, news);
        } else {
            HashMap<Long, String> news = new HashMap<>();
            news.put(millis, e.getMessage());

            HashMap<Integer, HashMap<Long, String>> news2 = new HashMap<>();
            news2.put(1, news);

            playerhashmap.put(p, news2);
        }
    }
}
