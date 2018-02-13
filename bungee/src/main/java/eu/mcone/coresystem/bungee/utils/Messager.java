/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils;

import eu.mcone.coresystem.bungee.CoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Messager {

    public static void send(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(CoreSystem.sqlconfig.getConfigValue("System-Prefix"))).append(TextComponent.fromLegacyText(message)).create());
    }

    public static void send(final ProxiedPlayer pp, final TextComponent tc) {
        TextComponent realTc = new TextComponent(CoreSystem.sqlconfig.getConfigValue("System-Prefix"));
        realTc.addExtra(tc);
        pp.sendMessage(realTc);
    }

    public static void send(final CommandSender sender, final String message) {
        sender.sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(CoreSystem.sqlconfig.getConfigValue("System-Prefix"))).append(TextComponent.fromLegacyText(message)).create());
    }

    public static void sendSimple(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(message)).create());
    }

    public static void sendSimple(final ProxiedPlayer pp, final TextComponent tc) {
        pp.sendMessage(tc);
    }

    public static void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(message)).create());
    }

    public static void sendSimple(final CommandSender sender, final TextComponent tc) {
        sender.sendMessage(tc);
    }

    public static void sendParty(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(CoreSystem.sqlconfig.getConfigValue("Party-Prefix"))).append(TextComponent.fromLegacyText(message)).create());
    }

    public static void sendFriend(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(CoreSystem.sqlconfig.getConfigValue("Friend-Prefix"))).append(TextComponent.fromLegacyText(message)).create());
    }

    public static void console(final String message) {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }

}
