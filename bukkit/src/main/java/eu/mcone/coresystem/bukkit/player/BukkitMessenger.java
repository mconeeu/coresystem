/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.broadcast.Broadcast;
import eu.mcone.coresystem.api.bukkit.broadcast.BroadcastMessage;
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
            sendBroadcast(broadcast, false);
        }
    }

    @Override
    public void broadcastSimple(Broadcast broadcast) {
        BroadcastEvent event = new BroadcastEvent(broadcast, true);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            sendBroadcast(broadcast, true);
        }
    }

    private void sendBroadcast(Broadcast broadcast, boolean simple) {
        if (broadcast.isSendMainMessage()) {
            sendBroadcastMessage(broadcast.getMainMessage(), simple);
        }

        for (BroadcastMessage message : broadcast.getAdditionalMessages()) {
            sendBroadcastMessage(message, simple);
        }
    }

    private void sendBroadcastMessage(BroadcastMessage message, boolean simple) {
        for (Player receiver : message.getReceivers()) {
            if (simple) {
                if (message.getTranslationReplacements() != null && message.getTranslationReplacements().length > 0) {
                    sendSimpleTransl(receiver, message.getMessageKey(), message.getTranslationReplacements());
                } else {
                    sendSimpleTransl(receiver, message.getMessageKey());
                }
            } else {
                if (message.getTranslationReplacements() != null && message.getTranslationReplacements().length > 0) {
                    sendTransl(receiver, message.getMessageKey(), message.getTranslationReplacements());
                } else {
                    sendTransl(receiver, message.getMessageKey());
                }
            }
        }
    }

}
