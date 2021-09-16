/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.menu.MenuInventory;
import eu.mcone.coresystem.api.bukkit.inventory.profile.ProfilePlayerInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CoreProfilePlayerInventory extends MenuInventory implements ProfilePlayerInventory {

    @Getter
    private final Player target;

    public CoreProfilePlayerInventory(Player p, Player t) {
        super(p, CoreProfileInventory.getTitle(t.getName()), MAX_ITEMS);
        this.target = t;
        CorePlayer ct = CoreSystem.getInstance().getCorePlayer(t);
        String status = ct.getState().getName();

        String[] lore;
        if (ct.isNicked()) {
            lore = new String[]{
                    ct.getNick().getGroup().getLabel(),
                    "",
                    "§7Coins: §f" + GlobalOfflineCorePlayer.NUMBERFORMAT.format(ct.getNick().getCoins()),
                    "§7Emeralds: §f" + GlobalOfflineCorePlayer.NUMBERFORMAT.format(0),
                    "§7Onlinetime: §f" + ct.getFormattedOnlinetime(),
                    "§7Status: " + status
            };
        } else {
            lore = new String[]{
                    ct.getMainGroup().getLabel(),
                    "",
                    "§7Coins: §f" + ct.getFormattedCoins(),
                    "§7Emeralds: §f" + ct.getFormattedEmeralds(),
                    "§7Onlinetime: §f" + ct.getFormattedOnlinetime(),
                    "§7Status: " + status
            };
        }

        setInfoItem(
                Skull.fromMojangValue(ct.getSkin().getValue(), 1)
                        .toItemBuilder()
                        .displayName("§f§l" + t.getName())
                        .lore(lore)
                        .create()
        );

        addMenuItem(GLOBE_HEAD.setDisplayName("§f§lOnline-Profil").lore("§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").getItemStack(), e -> {
            TextComponent tc0 = new TextComponent("§2Das Profil von " + t.getName() + " findest du ");

            TextComponent tc = new TextComponent();
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/u/" + p.getName()));
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Browser öffnen").create()));
            tc.setText(ChatColor.DARK_GREEN + "§f§nhier");

            tc0.addExtra(tc);
            Msg.send(p, tc);
            p.closeInventory();
        });

        addMenuItem(new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§f§lStats").lore("§7§oRufe die Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            p.performCommand("stats " + p.getName());
            Sound.click(p);
        });

        addMenuItem(new ItemBuilder(Material.CAKE, 1, 0).displayName("§f§lIn Party einladen").lore("§7§oLade diesen Spieler", "§7§oin deine §5§oParty§7§o ein.", "", "§8» §f§nLinksklick§8 | §7§oEinladen").create(), e -> {
            p.closeInventory();
            CoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party invite " + t.getName());
        });

        CoreSystem.getInstance().getChannelHandler().createGetRequest(p, friendString -> {
            boolean isFriend = false;
            for (String friend : friendString.split(",")) {
                if (friend.contains(t.getName())) isFriend = true;
            }

            if (isFriend) {
                addMenuItem(new ItemBuilder(Material.BARRIER, 1, 0).displayName("§c§lFreund entfernen").lore("§4§oLösche §c§o"+t.getName(), "§4§oaus deiner Freundesliste.", "", "§8» §f§nLinksklick§8 | §7§oLöschen").create(), e -> {
                    p.closeInventory();
                    CoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "friend remove " + t.getName());
                });
            } else {
                addMenuItem(new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§f§lFreund hinzufügen").lore("§7§oFüge "+t.getName(), "§7§ozu deiner Freundesliste", "§7§ohinzu.", "", "§8» §f§nLinksklick§8 | §7§oHinzufügen").create(), e -> {
                    p.closeInventory();
                    CoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "friend add " + t.getName());
                });
            }

            openInventory();
        }, "FRIENDS");
    }

}
