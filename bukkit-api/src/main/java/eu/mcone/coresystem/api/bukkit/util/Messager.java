/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.BroadcastMessageEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Messager {

    @Getter
    private String prefixTranslation;

    public Messager(String prefixTranlation) {
        this.prefixTranslation = prefixTranlation;
    }

    /**
     * send message with prefix to player
     *
     * @param player  player
     * @param message message
     */
    public void send(final Player player, final String message) {
        player.sendMessage(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, CoreSystem.getInstance().getCorePlayer(player)) + message);
    }

    /**
     * send TextComponent with prefix to player
     *
     * @param player        player
     * @param textComponent text component
     */
    public void send(final Player player, final TextComponent textComponent) {
        TextComponent realTc = new TextComponent(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, CoreSystem.getInstance().getCorePlayer(player)));
        realTc.addExtra(textComponent);
        player.spigot().sendMessage(realTc);
    }

    /**
     * send message with prefix to command sender
     *
     * @param sender  command sender
     * @param message message
     */
    public void send(final CommandSender sender, final String message) {
        sender.sendMessage(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, Language.ENGLISH) + message);
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
     * send Translation with prefix to command sender
     *
     * @param sender      command sender
     * @param translation translation name/key
     */
    public void sendTransl(final CommandSender sender, String... translation) {
        StringBuilder sb = new StringBuilder(CoreSystem.getInstance().getTranslationManager().get(prefixTranslation, Language.ENGLISH));
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, Language.ENGLISH));
        }
        sender.sendMessage(sb.toString());
    }

    /**
     * send Translation with prefix to player
     *
     * @param player      player
     * @param translation translation name/key
     */
    public void sendSimpleTransl(final Player player, String... translation) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(player);
        StringBuilder sb = new StringBuilder();
        for (String s : translation) {
            sb.append(CoreSystem.getInstance().getTranslationManager().get(s, cp));
        }
        player.sendMessage(sb.toString());
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
        Broadcast broadcast = new Broadcast(this, messageTyp, message, players).send();
        Bukkit.getPluginManager().callEvent(new BroadcastMessageEvent(broadcast));
        return broadcast.sendSimple();
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
    public static class Broadcast {
        private Messager messager;
        private BroadcastMessageTyp messageTyp;
        @BsonIgnore
        private transient final String message;
        @BsonIgnore
        private Player[] players;

        public Broadcast(Messager messager, final String message) {
            this(messager, BroadcastMessageTyp.INFO_MESSAGE, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }

        public Broadcast(Messager messager, final BroadcastMessageTyp typ, final String message) {
            this(messager, typ, message, Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }

        public Broadcast(Messager messager, final BroadcastMessageTyp typ, final String message, Player... players) {
            this.messager = messager;
            this.messageTyp = typ;
            this.message = message;
            this.players = players;
        }

        @BsonCreator
        public Broadcast(@BsonProperty("message") final BroadcastMessageTyp messageTyp, @BsonProperty("message") final String message, @BsonProperty("players") Player[] players) {
            this.message = message;
            this.players = players;
            this.messageTyp = messageTyp;
        }

        public Broadcast sendSimple() {
            for (Player player : players) {
                System.out.println(player.getName());
                player.sendMessage(message);
            }

            return this;
        }

        public Broadcast send() {
            for (Player player : players) {
                System.out.println(player.getName());
                messager.send(player, message);
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
