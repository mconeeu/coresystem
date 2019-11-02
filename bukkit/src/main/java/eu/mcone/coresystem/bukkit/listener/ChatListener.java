/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.VanishChatCMD;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @Getter @Setter
    private static boolean enabled = true;
    @Getter
    private static int cooldown = 0;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (enabled && !e.isCancelled()) {
            Player p = e.getPlayer();
            CorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(p);

            if (!cp.isVanished() || VanishChatCMD.usingCommand.contains(p.getUniqueId())) {
                if (
                        cooldown > 0
                                && !CoreSystem.getInstance().getCooldownSystem().addAndCheck(CoreSystem.getInstance(), getClass(), p.getUniqueId())
                                && !p.hasPermission("system.bukkit.chat.cooldown.bypass")
                ) {
                    CoreSystem.getInstance().getMessager().send(p, "Bitte warte " + cooldown + " Sekunden bevor du eine neue Nachricht schreibst!");
                    e.setCancelled(true);
                    return;
                }

                String playerMessage = e.getMessage();
                for (Player receiver : Bukkit.getOnlinePlayers()) {
                    if (receiver != p) {
                        String targetMessage;

                        if (e.getMessage().contains(receiver.getName())) {
                            if (e.getMessage().contains("@" + receiver.getName())) {
                                targetMessage = e.getMessage().replaceAll("@" + receiver.getName(), "§b@" + receiver.getName() + "§7");
                                playerMessage = playerMessage.replaceAll("@" + receiver.getName(), ChatColor.AQUA + "@" + receiver.getName() + ChatColor.GRAY);
                            } else {
                                targetMessage = e.getMessage().replaceAll(receiver.getName(), "§b@" + receiver.getName() + "§7");
                                playerMessage = playerMessage.replaceAll(receiver.getName(), "§b@" + receiver.getName() + "§7");
                            }

                            e.getRecipients().remove(receiver);
                            receiver.sendMessage((cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + BukkitCoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName()) + targetMessage);
                            receiver.playSound(receiver.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                        }
                    }
                }

                e.setFormat(
                        (cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + BukkitCoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName())
                                + "%2$s"
                );

                e.getRecipients().remove(p);
                p.sendMessage((cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + BukkitCoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName()) + playerMessage);
                VanishChatCMD.usingCommand.remove(p.getUniqueId());
            } else {
                e.setCancelled(true);
                CoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/vc <message>§4 um eine Chatnachricht zu schreiben während du im Vanish-Modus bist!");
            }
        }
    }

    public static void setCooldown(int cooldown) {
        ChatListener.cooldown = cooldown;
        BukkitCoreSystem.getInstance().getCooldownSystem().setCustomCooldownFor(ChatListener.class, cooldown);
    }

}