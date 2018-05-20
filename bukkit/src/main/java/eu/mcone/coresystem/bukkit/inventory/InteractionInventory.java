/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class InteractionInventory extends CoreInventory {

    public InteractionInventory(Player p, Player clicked) {
        super("§8» §3Interaktionsmenü", p, 27, CoreInventory.Option.FILL_EMPTY_SLOTS);

        BukkitCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT status, coins, onlinetime FROM userinfo WHERE uuid='" + clicked.getUniqueId().toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    double onlinetime = Math.floor((rs.getInt("onlinetime") / 60) * 100) / 100;
                    int coins = rs.getInt("coins");
                    String status = CoreSystem.getInstance().getCorePlayer(clicked).getStatus().getName();

                    setItem(4, ItemBuilder.createSkullItem(clicked.getName(), 1).displayName("§f§l" + clicked.getName()).lore(
                                CoreSystem.getInstance().getCorePlayer(clicked).getMainGroup().getLabel(),
                                "",
                                "§7Coins: §f" + coins ,
                                "§7Onlinetime: §f" + onlinetime + " Stunden", "§7Status: " + status
                            ).create()
                    );

                    setItem(20, ItemBuilder.createSkullItemFromURL("http://textures.minecraft.net/texture/6f74f58f541342393b3b16787dd051dfacec8cb5cd3229c61e5f73d63947ad", 1).displayName("§7Online-Profil Ansehen").create(), () -> {
                        TextComponent tc0 = new TextComponent(TextComponent.fromLegacyText(CoreSystem.getInstance().getTranslationManager().get("lobby.prefix") + "§2Das Profil von " + clicked.getName() + " findest du "));

                        TextComponent tc = new TextComponent();
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/user.php?uuid=" + clicked.getUniqueId()));
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Browser öffnen").create()));
                        tc.setText(ChatColor.DARK_GREEN + "§f§l§nhier");

                        tc0.addExtra(tc);
                        p.spigot().sendMessage(tc0);
                        p.closeInventory();
                    });

                    BukkitCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT uuid FROM `bungeesystem_friends` WHERE `uuid`='"+player.getUniqueId()+"' AND `target`='"+clicked.getUniqueId()+"' AND `key`='friend';", rs1 -> {
                        try {
                            if (rs1.next()) {
                                setItem(22, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§4Freund entfernen").create(), () -> {
                                    p.closeInventory();
                                    CoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD", "friend remove " + clicked);
                                });
                            } else {
                                setItem(22, new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§7Freund hinzufügen").create(), () -> {
                                    p.closeInventory();
                                    CoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD", "friend add " + clicked);
                                });
                            }

                            setItem(24, new ItemBuilder(Material.CAKE, 1, 0).displayName("§7In §5Party §7einladen").create(), () -> {
                                p.closeInventory();
                                CoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD", "party invite " + clicked);
                            });

                            openInventory();
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}