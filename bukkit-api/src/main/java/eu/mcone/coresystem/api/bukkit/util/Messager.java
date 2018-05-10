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

    /**
     * send message with prefix to player
     * @param player player
     * @param message message
     */
    public static void send(final Player player, final String message) {
        player.sendMessage(CoreSystem.getInstance().getTranslationManager().get("system.prefix", CoreSystem.getInstance().getCorePlayer(player)) + message);
    }

    /**
     * send TextComponent with prefix to player
     * @param player player
     * @param textComponent text component
     */
    public static void send(final Player player, final TextComponent textComponent) {
        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get("system.prefix", CoreSystem.getInstance().getCorePlayer(player)));
        realTc.addExtra(textComponent);
        player.spigot().sendMessage(realTc);
    }

    /**
     * send message with prefix to command sender
     * @param sender command sender
     * @param message message
     */
    public static void send(final CommandSender sender, final String message) {
        sender.sendMessage(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH) + message);
    }

    /**
     * send Translation with prefix to player
     * @param player player
     * @param translation translation name/key
     */
    public static void sendTransl(final Player player, String... translation) {
        BukkitCorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get("system.prefix", cp));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp));
        }
        player.sendMessage(sb.toString());
    }

    /**
     * send Translation with prefix to command sender
     * @param sender command sender
     * @param translation translation name/key
     */
    public static void sendTransl(final CommandSender sender, String... translation) {
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, Language.ENGLISH));
        }
        sender.sendMessage(sb.toString());
    }

    /**
     * send message to player
     * @param player player
     * @param message message
     */
    public static void sendSimple(final Player player, final String message) {
        player.sendMessage(message);
    }

    /**
     * send TextComponent to player
     * @param player player
     * @param textComponent text component
     */
    public static void sendSimple(final Player player, final TextComponent textComponent) {
        player.spigot().sendMessage(textComponent);
    }

    /**
     * send message to command sender
     * @param sender command sender
     * @param message message
     */
    public static void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(message);
    }

    /**
     * send message to console
     * @param message message
     */
    public static void console(final String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

}
