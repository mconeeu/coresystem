/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.util;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.core.translation.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class Messager {

    public static void send(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH) + message)));
    }

    public static void send(final ProxiedPlayer pp, final TextComponent tc) {
        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH));
        realTc.addExtra(tc);
        pp.sendMessage(realTc);
    }

    public static void send(final CommandSender sender, final String message) {
        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix", Language.ENGLISH) + message)));
    }

    public static void sendSimple(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }

    public static void sendSimple(final ProxiedPlayer pp, final TextComponent tc) {
        pp.sendMessage(tc);
    }

    public static void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }

    public static void sendSimple(final CommandSender sender, final TextComponent tc) {
        sender.sendMessage(tc);
    }

    public static void sendParty(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix.party", Language.ENGLISH) + message)));
    }

    public static void sendFriend(final ProxiedPlayer pp, final String message) {
        pp.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix.friend", Language.ENGLISH) + message)));
    }

    public static void console(final String message) {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }

}
