/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.util;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.chat.MarkdownParser;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import eu.mcone.coresystem.api.core.translation.TranslationManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class Messenger {

    private final String prefixTranslation;

    public Messenger(String prefixTranslation) {
        this.prefixTranslation = prefixTranslation;
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void send(ProxiedPlayer player, String message) {
        send(player, TextLevel.INFO, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendSuccess(ProxiedPlayer player, String message) {
        send(player, TextLevel.SUCCESS, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendInfo(ProxiedPlayer player, String message) {
        send(player, TextLevel.INFO, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendWarning(ProxiedPlayer player, String message) {
        send(player, TextLevel.WARNING, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendError(ProxiedPlayer player, String message) {
        send(player, TextLevel.ERROR, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void send(ProxiedPlayer player, TextLevel level, String message) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);

        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + (!level.equals(TextLevel.NONE) ? MarkdownParser.parseMarkdown(message, level) : message))));
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void send(CommandSender sender, String message) {
        send(sender, TextLevel.NONE, message);
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void sendInfo(CommandSender sender, String message) {
        send(sender, TextLevel.INFO, message);
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void sendSuccess(CommandSender sender, String message) {
        send(sender, TextLevel.SUCCESS, message);
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void sendWarning(CommandSender sender, String message) {
        send(sender, TextLevel.WARNING, message);
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void sendError(CommandSender sender, String message) {
        send(sender, TextLevel.ERROR, message);
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void send(final CommandSender sender, TextLevel level, final String message) {
        CorePlayer cp = sender instanceof ProxiedPlayer ? CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender) : null;

        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE)
                + (!level.equals(TextLevel.NONE) ? MarkdownParser.parseMarkdown(message, level) : message)
        )));
    }

    /**
     * send TextComponent with prefix to player
     *
     * @param player        player
     * @param textComponent text component
     */
    public void send(final ProxiedPlayer player, final BaseComponent... textComponent) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);

        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ));
        for (BaseComponent bc : textComponent) {
            realTc.addExtra(bc);
        }

        player.sendMessage(realTc);
    }

    /**
     * send Translation with prefix to player
     *
     * @param player      player
     * @param translation translation name/key
     */
    public void sendTransl(final ProxiedPlayer player, String... translation) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);

        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, cp));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp));
        }

        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(sb.toString())));
    }

    /**
     * send Translation with prefix to player
     *
     * @param player         player
     * @param translationKey translation name/key
     */
    public void sendTransl(final ProxiedPlayer player, String translationKey, Object... replacements) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        String translation = CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, cp, replacements)
                + CoreSystem.getInstance().getTranslationManager().get(translationKey, cp, replacements);

        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(translation)));
    }

    /**
     * send Translation with prefix to command sender
     *
     * @param sender      command sender
     * @param translation translation name/key
     */
    public void sendTransl(final CommandSender sender, String... translation) {
        CorePlayer cp = sender instanceof ProxiedPlayer ? CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender) : null;

        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE));
        }

        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(sb.toString())));
    }

    /**
     * send Translation with prefix to command sender
     *
     * @param sender         command sender
     * @param translationKey translation name/key
     */
    public void sendTransl(final CommandSender sender, String translationKey, Object... replacements) {
        CorePlayer cp = sender instanceof ProxiedPlayer ? CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender) : null;

        String translation = CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + CoreSystem.getInstance().getTranslationManager().get(
                translationKey,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE,
                replacements
        );

        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(translation)));
    }

    /**
     * send message to player
     *
     * @param player  player
     * @param message message
     */
    public void sendSimple(final ProxiedPlayer player, final String message) {
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }


    /**
     * send TextComponent to player
     *
     * @param player        player
     * @param textComponent text component
     */
    public void sendSimple(final ProxiedPlayer player, final TextComponent textComponent) {
        player.sendMessage(textComponent);
    }

    /**
     * send message to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(new TextComponent(TextComponent.fromLegacyText(message)));
    }

    /**
     * send TextComponent to command sender
     *
     * @param sender        command sender
     * @param textComponent text component
     */
    public void sendSimple(final CommandSender sender, final TextComponent textComponent) {
        sender.sendMessage(textComponent);
    }

    /**
     * send message with party prefix
     *
     * @param player  command sender
     * @param message text component
     */
    public void sendParty(final ProxiedPlayer player, final String message) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix.party", cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE) + message)));
    }

    /**
     * send message with friend prefix
     *
     * @param player  player
     * @param message message
     */
    public void sendFriend(final ProxiedPlayer player, final String message) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        player.sendMessage(new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("system.prefix.friend", cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE) + message)));
    }

}
