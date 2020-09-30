/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.broadcast.Broadcast;
import eu.mcone.coresystem.api.bukkit.broadcast.Messenger;
import eu.mcone.coresystem.api.bukkit.event.broadcast.BroadcastEvent;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.player.CoreMessenger;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitMessenger extends CoreMessenger<Player, CommandSender> implements Messenger {

    public BukkitMessenger(GlobalCoreSystem coreSystem, String prefixTranslation) {
        super(coreSystem, prefixTranslation);
    }

    @Override
    protected void dispatchMessage(Player player, String message) {
        player.sendMessage(message);
    }

    @Override
    protected void dispatchMessage(Player player, BaseComponent... baseComponents) {
        player.spigot().sendMessage(baseComponents);
    }

    @Override
    protected GlobalCorePlayer getCorePlayer(Player player) {
        return (BukkitCorePlayer) BukkitCoreSystem.getSystem().getCorePlayer(player);
    }

    @Override
    protected void dispatchSenderMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    @Override
    protected void dispatchSenderMessage(CommandSender sender, BaseComponent... baseComponents) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(baseComponents);
        } else {
            dispatchSenderMessage(sender, TextComponent.toLegacyText(baseComponents));
        }
    }

    @Override
    public void broadcast(Broadcast broadcast) {
        BroadcastEvent event = new BroadcastEvent(broadcast, false);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (Player player : broadcast.getPlayers()) {
                if (broadcast.getPlayers() != null && broadcast.getPlayers().length > 0) {
                    sendTransl(player, broadcast.getMessageKey(), broadcast.getTranslationReplacements());
                } else {
                    sendTransl(player, broadcast.getMessageKey());
                }
            }
        }
    }

    @Override
    public void broadcastSimple(Broadcast broadcast) {
        BroadcastEvent event = new BroadcastEvent(broadcast, true);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (Player player : broadcast.getPlayers()) {
                if (broadcast.getPlayers() != null && broadcast.getPlayers().length > 0) {
                    sendSimpleTransl(player, broadcast.getMessageKey(), broadcast.getTranslationReplacements());
                } else {
                    sendSimpleTransl(player, broadcast.getMessageKey());
                }
            }
        }
    }

}
