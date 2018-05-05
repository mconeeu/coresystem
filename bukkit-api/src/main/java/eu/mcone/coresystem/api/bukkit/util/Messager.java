/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Messager {

    public static void send(final Player p, final String message) {
        p.sendMessage(CoreSystem.getInstance().getTranslationManager().get("system.prefix", CoreSystem.getInstance().getCorePlayer(p)) + message);
    }

    public static void send(final Player p, final TextComponent tc) {
        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get("system.prefix", CoreSystem.getInstance().getCorePlayer(p)));
        realTc.addExtra(tc);
        p.spigot().sendMessage(realTc);
    }

    public static void send(final CommandSender sender, final String message) {
        sender.sendMessage(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH) + message);
    }

    public static void sendTransl(final Player p, String... translation) {
        BukkitCorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get("system.prefix", cp));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp));
        }
        p.sendMessage(sb.toString());
    }

    public static void sendTransl(final CommandSender sender, String... translation) {
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, Language.ENGLISH));
        }
        sender.sendMessage(sb.toString());
    }

    public static void sendSimple(final Player p, final String message) {
        p.sendMessage(message);
    }

    public static void sendSimple(final Player p, final TextComponent tc) {
        p.spigot().sendMessage(tc);
    }

    public static void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(message);
    }

    public static void sendParty(final Player p, final String message) {
        p.sendMessage(CoreSystem.getInstance().getTranslationManager().get("system.prefix.party", CoreSystem.getInstance().getCorePlayer(p)) + message);
    }

    public static void sendFriend(final Player p, final String message) {
        p.sendMessage(CoreSystem.getInstance().getTranslationManager().get("system.prefix.friend", CoreSystem.getInstance().getCorePlayer(p)) + message);
    }

    public static void console(final String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

}
