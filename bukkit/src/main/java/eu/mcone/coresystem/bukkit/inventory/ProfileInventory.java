/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.ProfileInventoryModifier;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ProfileInventory extends CoreInventory {

    @Setter
    private static int size;
    private static List<ProfileInventoryModifier> modifiers = new ArrayList<>();

    public ProfileInventory(CorePlayer corePlayer) {
        super("§8» §3§l" + corePlayer.getName() + "'" + (!corePlayer.getName().endsWith("s") && !corePlayer.getName().endsWith("S") ? "s" : "") + " Profil", corePlayer.bukkit(), (size > 0) ? size : InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);
        Player p = corePlayer.bukkit();

        double onlinetime = Math.floor(((double) corePlayer.getOnlinetime() / 60 / 60) * 100) / 100;
        String status = corePlayer.getState().getName();

        if (corePlayer.isNicked()) {
            setItem(InventorySlot.ROW_1_SLOT_5, new Skull(corePlayer.getName(), 1).toItemBuilder().displayName("§f§l" + corePlayer.getName()).lore(
                    corePlayer.getMainGroup().getLabel(),
                    corePlayer.getNick().getGroup().getLabel(),
                    "",
                    "§7Coins: §f" + corePlayer.getFormattedCoins(),
                    "§7Emeralds: §f" + corePlayer.getFormattedEmeralds(),
                    "§7Onlinetime: §f" + onlinetime + " Stunden",
                    "§7Status: " + status
                    ).create()
            );
        } else {
            setItem(InventorySlot.ROW_1_SLOT_5, new Skull(corePlayer.getName(), 1).toItemBuilder().displayName("§f§l" + corePlayer.getName()).lore(
                    corePlayer.getMainGroup().getLabel(),
                    "",
                    "§7Coins: §f" + corePlayer.getFormattedCoins(),
                    "§7Emeralds: §f" + corePlayer.getFormattedEmeralds(),
                    "§7Onlinetime: §f" + onlinetime + " Stunden",
                    "§7Status: " + status
                    ).create()
            );
        }

        setItem(InventorySlot.ROW_3_SLOT_2, new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§f§lStats").lore("§7§oRufe die deine Spielerstatistiken", "§7§oaus allen MC ONE Spielmodi ab!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            p.performCommand("stats");
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§9§lFreunde").lore("§7§oZeige deine Freunde und", "§7§oFreundschaftsanzeigen", "§7§oan!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new FriendsInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.CAKE, 1, 0).displayName("§5§lParty").lore("§7§oZeige infos zu deiner Party", "§7§oan, oder ertselle eine!", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new PartyInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        setItem(InventorySlot.ROW_3_SLOT_8, new ItemBuilder(Material.REDSTONE, 1, 0).displayName("§c§lEinstellungen").lore("§7§oVerwalte dein Konto und andere", "§7§oingame Einstellungen", "", "§8» §f§nLinksklick§8 | §7§oÖffnen").create(), e -> {
            new PlayerSettingsInventory(p);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        });

        for (ProfileInventoryModifier modifier : modifiers) {
            modifier.onCreate(this, p);
        }

        openInventory();
    }

    public static void addModifier(ProfileInventoryModifier modifier) {
        modifiers.add(modifier);
    }

}
