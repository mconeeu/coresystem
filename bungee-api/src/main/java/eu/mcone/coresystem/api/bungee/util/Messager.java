/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.util;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class Messager {

    private String prefixTranslation;

    public Messager(String prefixTranslation) {
        this.prefixTranslation = prefixTranslation;
    }

    /**
     * send message with prefix to player
     * @param player player
     * @param message message
     */
    public void send(final ProxiedPlayer player, final String message) {
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, Language.ENGLISH) + message)));
    }

    /**
     * send TextComponent with prefix to player
     * @param player player
     * @param textComponent text component
     */
    public void send(final ProxiedPlayer player, final TextComponent textComponent) {
        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, Language.ENGLISH));
        realTc.addExtra(textComponent);
        player.sendMessage(realTc);
    }

    /**
     * send message with prefix to command sender
     * @param sender command sender
     * @param message message
     */
    public void send(final CommandSender sender, final String message) {
        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, Language.ENGLISH) + message)));
    }

    /**
     * send Translation with prefix to player
     * @param player player
     * @param translation translation name/key
     */
    public void sendTransl(final ProxiedPlayer player, String... translation) {
        BungeeCorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, cp));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp));
        }
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(sb.toString())));
    }

    /**
     * send Translation with prefix to command sender
     * @param sender command sender
     * @param translation translation name/key
     */
    public void sendTransl(final CommandSender sender, String... translation) {
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, Language.ENGLISH));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, Language.ENGLISH));
        }
        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(sb.toString())));
    }

    /**
     * send message to player
     * @param player player
     * @param message message
     */
    public void sendSimple(final ProxiedPlayer player, final String message) {
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }


    /**
     * send TextComponent to player
     * @param player player
     * @param textComponent text component
     */
    public void sendSimple(final ProxiedPlayer player, final TextComponent textComponent) {
        player.sendMessage(textComponent);
    }

    /**
     * send message to command sender
     * @param sender command sender
     * @param message message
     */
    public void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }

    /**
     * send TextComponent to command sender
     * @param sender command sender
     * @param textComponent text component
     */
    public void sendSimple(final CommandSender sender, final TextComponent textComponent) {
        sender.sendMessage(textComponent);
    }

    /**
     * send message with party prefix
     * @param player command sender
     * @param message text component
     */
    public void sendParty(final ProxiedPlayer player, final String message) {
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix.party", Language.ENGLISH) + message)));
    }

    /**
     * send message with friend prefix
     * @param player player
     * @param message message
     */
    public void sendFriend(final ProxiedPlayer player, final String message) {
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix.friend", Language.ENGLISH) + message)));
    }

}
