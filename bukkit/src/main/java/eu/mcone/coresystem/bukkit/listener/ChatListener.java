/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.facades.Transl;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.VanishChatCMD;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @Getter
    @Setter
    private static boolean enabled = true;
    @Getter
    private static int cooldown = 0;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (enabled && !e.isCancelled()) {
            Player p = e.getPlayer();
            CorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(p);

            if (p.hasPermission("group.team")) {
                for (CorePlayer player : CoreSystem.getInstance().getOnlineCorePlayers()) {
                    if (player.isVanished() && e.getMessage().contains(player.getName())) {
                        BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "tc " + e.getMessage());
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            if (!cp.isVanished() || VanishChatCMD.chatEnabled.contains(p.getUniqueId())) {
                if (
                        cooldown > 0
                                && !CoreSystem.getInstance().getCooldownSystem().addAndCheck(getClass(), p.getUniqueId())
                                && !p.hasPermission("system.bukkit.chat.cooldown.bypass")
                ) {
                    CoreSystem.getInstance().getMessenger().send(p, "Bitte warte " + cooldown + " Sekunden bevor du eine neue Nachricht schreibst!");
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
                            receiver.sendMessage((cp.isNicked() ? cp.getNick().getGroup().getPrefix() : cp.getMainGroup().getPrefix()) + Transl.get("system.chat").replaceAll("%Player%", p.getName()) + targetMessage);
                            Sound.done(p);
                        }
                    }
                }

                e.setFormat(
                        (cp.isNicked() ? cp.getNick().getGroup().getPrefix() : cp.getMainGroup().getPrefix()) + Transl.get("system.chat").replaceAll("%Player%", p.getName())
                                + "%2$s"
                );

                e.getRecipients().remove(p);
                p.sendMessage((cp.isNicked() ? cp.getNick().getGroup().getPrefix() : cp.getMainGroup().getPrefix()) + Transl.get("system.chat", p).replaceAll("%Player%", p.getName()) + playerMessage);
            } else {
                e.setCancelled(true);
                CoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze §c/vc on§4 um eine Chatnachricht zu schreiben während du im Vanish-Modus bist!");
            }
        }
    }

    public static void setCooldown(int cooldown) {
        ChatListener.cooldown = cooldown;
        BukkitCoreSystem.getInstance().getCooldownSystem().setCustomCooldownFor(ChatListener.class, cooldown);
    }
}