/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.facades.Transl;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.TranslationManager;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.VanishChatCMD;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChatListener implements Listener {

    @Getter
    @Setter
    private static boolean enabled = true;
    @Getter
    private static int cooldown = 0;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatLow(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);
        Group g = cp.isNicked() ? cp.getNick().getGroup() : cp.getMainGroup();

        e.setFormat(
                Transl.get("system.bukkit.chat", TranslationManager.DEFAULT_LANGUAGE, g.getPrefix() + "%1$s", "%2$s")
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        if (enabled && !e.isCancelled()) {
            Player p = e.getPlayer();
            CorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(p);

            if (p.hasPermission("group.team")) {
                for (CorePlayer player : CoreSystem.getInstance().getOnlineCorePlayers()) {
                    for (String part : e.getMessage().split(" ")) {
                        if (player.hasPermission("group.team") && (player.isVanished() || player.isNicked()) && part.equalsIgnoreCase(player.getName())) {
                            BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "tc " + e.getMessage());
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }

            if (!cp.isVanished() || VanishChatCMD.chatEnabled.contains(p.getUniqueId())) {
                if (
                        cooldown > 0
                                && !CoreSystem.getInstance().getCooldownSystem().addAndCheck(getClass(), p.getUniqueId())
                                && !p.hasPermission("system.bukkit.chat.cooldown.bypass")
                ) {
                    Msg.send(p, "Bitte warte " + cooldown + " Sekunden bevor du eine neue Nachricht schreibst!");
                    e.setCancelled(true);
                    return;
                }

                String message = e.getMessage();
                LinkedHashMap<TextComponent, Boolean> parts = new LinkedHashMap<>();

                String[] words = message.split(" ");
                boolean first = true;
                wordLoop:
                for (String word : words) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (word.equalsIgnoreCase(player.getName()) || word.equalsIgnoreCase("@"+player.getName())) {
                            TextComponent component = new TextComponent((!first ? " " : "") + "@"+player.getName());
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/profile "+player.getName()));
                            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7§oProfil anzeigen")));

                            parts.put(component, true);
                            first = false;
                            continue wordLoop;
                        }
                    }

                    Map.Entry<TextComponent, Boolean> lastEntry = null;
                    if (parts.size() > 0) {
                        lastEntry = new ArrayList<>(parts.entrySet()).get(parts.size()-1);
                    }

                    if (lastEntry != null && !lastEntry.getValue()) {
                        lastEntry.getKey().setText(lastEntry.getKey().getText() + " " + word);
                    } else {
                        TextComponent component = new TextComponent((!first ? " " : "") + word);
                        component.setColor(net.md_5.bungee.api.ChatColor.GRAY);

                        parts.put(component, false);
                    }

                    first = false;
                }

                Set<Player> recipients = e.getRecipients();
                for (Player recipient : recipients) {
                    TextComponent messageComponent = new TextComponent(String.format(e.getFormat(), p.getName(), ""));

                    for (Map.Entry<TextComponent, Boolean> partEntry : parts.entrySet()) {
                        TextComponent component = partEntry.getKey();

                        if (partEntry.getValue()) {
                            System.out.println("name: "+component.getText().substring(2) + " == "+recipient.getName());
                            if (component.getText().substring(2).equals(recipient.getName())) {
                                System.out.println("true");
                                component.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                                Sound.done(recipient);
                            } else {
                                System.out.println("false");
                                component.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
                            }
                        }

                        messageComponent.addExtra(component);
                    }

                    recipient.spigot().sendMessage(messageComponent);
                }

                e.getRecipients().clear();
            } else {
                e.setCancelled(true);
                Msg.send(p, "§4Bitte benutze §c/vc on§4 um eine Chatnachricht zu schreiben während du im Vanish-Modus bist!");
            }
        }
    }

    public static void setCooldown(int cooldown) {
        ChatListener.cooldown = cooldown;
        BukkitCoreSystem.getInstance().getCooldownSystem().setCustomCooldownFor(ChatListener.class, cooldown);
    }
}