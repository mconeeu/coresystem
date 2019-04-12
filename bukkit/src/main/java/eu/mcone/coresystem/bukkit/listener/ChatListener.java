/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    public static boolean disabled = false;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!disabled) {
            Player p = e.getPlayer();
            CorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(p);
            String message;

            if (cp.isNicked()) {
                message = Group.SPIELER.getPrefix() + BukkitCoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat")
                        .replaceAll("%Player%", p.getName())
                        .replaceAll("Nachricht", e.getMessage());
            } else {
                message = cp.getMainGroup().getPrefix() + BukkitCoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat")
                        .replaceAll("%Player%", p.getName())
                        .replaceAll("Nachricht", e.getMessage());
            }

            String playerMessage = message;
            for (Player receiver : Bukkit.getOnlinePlayers()) {
                if (receiver != p) {
                    String targetMessage;

                    if (message.contains(receiver.getName())) {
                        if (message.contains("@" + receiver.getName())) {
                            targetMessage = message.replaceAll("@" + receiver.getName(), "§b@" + receiver.getName() + "§7");
                            playerMessage = playerMessage.replaceAll("@" + receiver.getName(), ChatColor.AQUA + "@" + receiver.getName() + ChatColor.GRAY);
                        } else {
                            targetMessage = message.replaceAll(receiver.getName(), "§b@" + receiver.getName() + "§7");
                            playerMessage = playerMessage.replaceAll(receiver.getName(), "§b@" + receiver.getName() + "§7");
                        }

                        e.getRecipients().remove(receiver);
                        receiver.sendMessage(targetMessage);
                        receiver.playSound(receiver.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    }
                }
            }

            e.setFormat(message);

            e.getRecipients().remove(p);
            p.sendMessage(playerMessage);
        }
    }
}