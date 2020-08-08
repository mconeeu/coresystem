/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.BroadcastMessageEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.chat.MarkdownParser;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationManager;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Messenger {

    @Getter
    private final String prefixTranslation;

    public Messenger(String prefixTranlation) {
        this.prefixTranslation = prefixTranlation;
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void send(Player player, String message) {
        send(player, TextLevel.INFO, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendSuccess(Player player, String message) {
        send(player, TextLevel.SUCCESS, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendInfo(Player player, String message) {
        send(player, TextLevel.INFO, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendWarning(Player player, String message) {
        send(player, TextLevel.WARNING, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void sendError(Player player, String message) {
        send(player, TextLevel.ERROR, message);
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void send(Player player, TextLevel level, String message) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);

        player.sendMessage(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + MarkdownParser.parseMarkdown(message, level));
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
    public void send(CommandSender sender, TextLevel level, String message) {
        CorePlayer cp = sender instanceof Player ? CoreSystem.getInstance().getCorePlayer((Player) sender) : null;

        sender.sendMessage(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + (!level.equals(TextLevel.NONE) ? MarkdownParser.parseMarkdown(message, level) : message));
    }

    /**
     * send TextComponent with prefix to player
     *
     * @param player        player
     * @param textComponent text component
     */
    public void send(Player player, TextComponent textComponent) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);

        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ));
        realTc.addExtra(textComponent);

        player.spigot().sendMessage(realTc);
    }

    /**
     * send Translation with prefix to player
     *
     * @param player      player
     * @param translation translation name/key
     */
    public void sendTransl(final Player player, String... translation) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);

        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, cp));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp));
        }

        player.sendMessage(sb.toString());
    }

    /**
     * send Translation with prefix to player
     *
     * @param player         player
     * @param translationKey translation name/key
     */
    public void sendTransl(final Player player, String translationKey, Object... replacements) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        String translation = CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, cp, replacements)
                + CoreSystem.getInstance().getTranslationManager().get(translationKey, cp, replacements);

        player.sendMessage(translation);
    }

    /**
     * send Translation with prefix to command sender
     *
     * @param sender      command sender
     * @param translation translation name/key
     */
    public void sendTransl(final CommandSender sender, String... translation) {
        CorePlayer cp = sender instanceof Player ? CoreSystem.getInstance().getCorePlayer((Player) sender) : null;

        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, Language.ENGLISH));
        }

        sender.sendMessage(sb.toString());
    }

    /**
     * send Translation with prefix to command sender
     *
     * @param sender         command sender
     * @param translationKey translation name/key
     */
    public void sendTransl(final CommandSender sender, String translationKey, Object... replacements) {
        CorePlayer cp = sender instanceof Player ? CoreSystem.getInstance().getCorePlayer((Player) sender) : null;

        String translation = CoreSystem.getInstance().getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + CoreSystem.getInstance().getTranslationManager().get(
                translationKey,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE,
                replacements
        );

        sender.sendMessage(translation);
    }

    /**
     * send Translation with prefix to player
     *
     * @param player         player
     * @param translationKey translation name/key
     */
    public void sendSimpleTransl(final Player player, String translationKey, Object... replacements) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        String translation = CoreSystem.getInstance().getTranslationManager().get(translationKey, cp, replacements);

        player.sendMessage(translation);
    }

    /**
     * send message to player
     *
     * @param player  player
     * @param message message
     */
    public void sendSimple(final Player player, final String message) {
        player.sendMessage(message);
    }

    /**
     * send TextComponent to player
     *
     * @param player        player
     * @param textComponent text component
     */
    public void sendSimple(final Player player, final TextComponent textComponent) {
        player.spigot().sendMessage(textComponent);
    }

    /**
     * send message to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void sendSimple(final CommandSender sender, final String message) {
        sender.sendMessage(message);
    }

    public Broadcast simpleBroadcast(final String message) {
        return this.simpleBroadcast(Broadcast.BroadcastMessageTyp.INFO_MESSAGE, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public Broadcast simpleBroadcast(final Broadcast.BroadcastMessageTyp typ, final String message) {
        return this.simpleBroadcast(typ, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public Broadcast simpleBroadcast(final Broadcast.BroadcastMessageTyp messageTyp, final String message, final Player... players) {
        return new Broadcast(this, messageTyp, message, players).sendSimple();
    }

    public Broadcast broadcast(final String message) {
        return this.broadcast(Broadcast.BroadcastMessageTyp.INFO_MESSAGE, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public Broadcast broadcast(final Broadcast.BroadcastMessageTyp typ, final String message) {
        return this.broadcast(typ, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public Broadcast broadcast(final Broadcast.BroadcastMessageTyp messageTyp, final String message, final Player... players) {
        Broadcast broadcast = new Broadcast(this, messageTyp, message, players);
        Bukkit.getPluginManager().callEvent(new BroadcastMessageEvent(broadcast));
        return broadcast.send();
    }

    @Getter
    @BsonDiscriminator
    public static class Broadcast {
        @BsonIgnore
        private transient Messenger messenger;
        @Getter
        private final transient BroadcastMessageTyp messageTyp;
        private String sMessageTyp = null;

        @Getter
        private final String message;
        @BsonIgnore
        private transient Player[] players;

        public Broadcast(Messenger messenger, final String message) {
            this(messenger, BroadcastMessageTyp.INFO_MESSAGE, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }

        public Broadcast(Messenger messenger, final BroadcastMessageTyp typ, final String message) {
            this(messenger, typ, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }

        public Broadcast(Messenger messenger, final BroadcastMessageTyp typ, final String message, Player... players) {
            this.messenger = messenger;

            this.messageTyp = typ;
            this.sMessageTyp = typ.toString();

            this.message = message;
            this.players = players;
        }

        @BsonCreator
        public Broadcast(@BsonProperty("sMessageTyp") String sMessageTyp, @BsonProperty("message") String message) {
            this.messageTyp = BroadcastMessageTyp.valueOf(sMessageTyp);
            this.message = message;
        }

        public Broadcast sendSimple() {
            for (Player player : players) {
                player.sendMessage(message);
            }

            return this;
        }

        public Broadcast send() {
            for (Player player : players) {
                messenger.send(player, message);
            }

            return this;
        }

        public enum BroadcastMessageTyp {
            INFO_MESSAGE,
            KILL_MESSAGE,
            DEATH_MESSAGE,
            GOAL_MESSAGE
        }
    }

}
