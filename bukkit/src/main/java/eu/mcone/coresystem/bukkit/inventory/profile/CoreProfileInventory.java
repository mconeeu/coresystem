/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.menu.MenuInventory;
import eu.mcone.coresystem.api.bukkit.inventory.profile.ProfileInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.inventory.FriendsInventory;
import eu.mcone.coresystem.bukkit.inventory.PartyInventory;
import eu.mcone.coresystem.bukkit.inventory.PlayerSettingsInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoreProfileInventory extends MenuInventory implements ProfileInventory {

    public CoreProfileInventory(Player p) {
        super(p, getTitle(p.getName()), MAX_ITEMS);
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);
        String status = cp.getState().getName();

        List<String> lore = new ArrayList<>(Arrays.asList(
                cp.getMainGroup().getLabel(),
                ""
        ));
        if (cp.isNicked()) {
            lore.addAll(Arrays.asList(
                    "§7Nick: " + cp.getNick().getGroup().getPrefix() + cp.getNick().getName(),
                    "§7Coins: §f" + cp.getFormattedCoins(),
                    "§7Emeralds: §f" + cp.getFormattedEmeralds(),
                    "§7Onlinetime: §f" + cp.getFormattedOnlinetime(),
                    "§7Status: " + status
            ));
        } else {
            lore.addAll(Arrays.asList(
                    "§7Coins: §f" + cp.getFormattedCoins(),
                    "§7Emeralds: §f" + cp.getFormattedEmeralds(),
                    "§7Onlinetime: §f" + cp.getFormattedOnlinetime(),
                    "§7Status: " + status
            ));
        }

        setInfoItem(
                Skull.fromMojangValue(cp.getSkin().getValue(), 1)
                        .toItemBuilder()
                        .displayName("§f§l" + cp.getName())
                        .lore(lore)
                        .create()
        );

        addMenuItem(new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§f§lStats").lore("§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            p.performCommand("stats");
            Sound.click(p);
        });

        addMenuItem(new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§9§lFreunde").lore("§7§oZeige deine Freunde und", "§7§oFreundschaftsanzeigen", "§7§oan!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new FriendsInventory(p);
            Sound.click(p);
        });

        addMenuItem(new ItemBuilder(Material.CAKE, 1, 0).displayName("§5§lParty").lore("§7§oZeige infos zu deiner Party", "§7§oan, oder ertselle eine!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new PartyInventory(p);
            Sound.click(p);
        });

        addMenuItem(new ItemBuilder(Material.REDSTONE, 1, 0).displayName("§c§lEinstellungen").lore("§7§oVerwalte dein Konto und andere", "§7§oingame Einstellungen", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new PlayerSettingsInventory(p);
            Sound.click(p);
        });

        openInventory();
    }

    static String getTitle(String name) {
        return "§8» §3§l" + name + "§8'" + (!name.endsWith("s") && !name.endsWith("S") ? "s" : "") + " Profil";
    }

}
