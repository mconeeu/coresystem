/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.translation.Language;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class PlayerSettingsInventory extends CoreInventory {

    PlayerSettingsInventory(Player p) {
        super("§8» §c§lEinstellungen", p, InventorySlot.ROW_4, Option.FILL_EMPTY_SLOTS);
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        setItem(InventorySlot.ROW_2_SLOT_3, new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§f§lErhalte Freundschaftsanfragen").create());
        setItem(InventorySlot.ROW_3_SLOT_3, cp.getSettings().isEnableFriendRequests() ?
                        new ItemBuilder(Material.INK_SACK, 1, 10).displayName("§a§lAktiviert").lore("§7§oKlicke zum deaktivieren").create() :
                        new ItemBuilder(Material.INK_SACK, 1, 1).displayName("§c§lDeaktiviert").lore("§7§oKlicke zum aktivieren").create(),
                e -> {
                    cp.getSettings().setEnableFriendRequests(!cp.getSettings().isEnableFriendRequests());
                    cp.updateSettings();
                    new PlayerSettingsInventory(p);
                }
        );

        setItem(InventorySlot.ROW_2_SLOT_4, new ItemBuilder(Material.BOOK_AND_QUILL, 1, 0).displayName("§f§lDatenschutzerklärung akzeptiert").create());
        setItem(InventorySlot.ROW_3_SLOT_4, cp.getSettings().isAcceptedAgbs() ?
                        new ItemBuilder(Material.INK_SACK, 1, 10).displayName("§a§lAkzeptiert").lore("§7§oWenn du die Datenschutzerklärung", "§7§onicht mehr akzeptierst, musst", "§7§oDu deinen Account löschen!", "", "§8» §f§nLinksklick§8 | §7§oKonto Öffnen").create() :
                        new ItemBuilder(Material.INK_SACK, 1, 1).displayName("§c§lNicht Akzeptiert").lore("§7§oKlicke zum akzeptieren").create(),
                e -> {
            if (cp.getSettings().isAcceptedAgbs()) {
                p.spigot().sendMessage(new ComponentBuilder(CoreSystem.getInstance().getTranslationManager().get("system.prefix.server"))
                        .append("§7Deinen Account kannst du auf unserer Homepage löschen. Öffne dafür dein ")
                        .color(ChatColor.GRAY)
                        .append("Online-Profil")
                        .color(ChatColor.WHITE)
                        .bold(true)
                        .underlined(true)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oBrowser öffnen").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/dashboard/account.php")).create());
                p.closeInventory();
            } else {
                cp.getSettings().setAcceptedAgbs(true);
                cp.updateSettings();
                new PlayerSettingsInventory(p);
            }
        });

        setItem(InventorySlot.ROW_2_SLOT_5, ItemBuilder.createSkullItemFromURL("http://textures.minecraft.net/texture/6f74f58f541342393b3b16787dd051dfacec8cb5cd3229c61e5f73d63947ad", 1).displayName("§f§lSprache").create());
        setItem(InventorySlot.ROW_3_SLOT_5, ItemBuilder.createSkullItemFromURL(cp.getSettings().getLanguage().getTextureUrl(), 1).displayName("§f§l"+cp.getSettings().getLanguage().getName()).lore("§7§oKlicke zum ändern").create(), e -> {
            switch (cp.getSettings().getLanguage()) {
                case ENGLISH: cp.getSettings().setLanguage(Language.GERMAN); break;
                case GERMAN: cp.getSettings().setLanguage(Language.FRENCH); break;
                case FRENCH: cp.getSettings().setLanguage(Language.ENGLISH); break;
            }

            cp.updateSettings();
            new PlayerSettingsInventory(p);
        });

        setItem(InventorySlot.ROW_2_SLOT_6, new ItemBuilder(Material.PAPER).displayName("§f§lPrivate Nachrichten erhalten").create());
        setItem(InventorySlot.ROW_3_SLOT_6, getSenderItem(cp.getSettings().getPrivateMessages()).lore("§7§oKlicke zum ändern").create(), e -> {
            switch (cp.getSettings().getPrivateMessages()) {
                case ALL: cp.getSettings().setPrivateMessages(PlayerSettings.Sender.FRIENDS); break;
                case FRIENDS: cp.getSettings().setPrivateMessages(PlayerSettings.Sender.NOBODY); break;
                case NOBODY: cp.getSettings().setPrivateMessages(PlayerSettings.Sender.ALL); break;
            }

            cp.updateSettings();
            new PlayerSettingsInventory(p);
        });

        setItem(InventorySlot.ROW_2_SLOT_7, new ItemBuilder(Material.CAKE).displayName("§f§lPartyanfragenanfragen erhalten").create());
        setItem(InventorySlot.ROW_3_SLOT_7, getSenderItem(cp.getSettings().getPrivateMessages()).lore("§7§oKlicke zum ändern").create(), e -> {
            switch (cp.getSettings().getPartyInvites()) {
                case ALL: cp.getSettings().setPartyInvites(PlayerSettings.Sender.FRIENDS); break;
                case FRIENDS: cp.getSettings().setPartyInvites(PlayerSettings.Sender.NOBODY); break;
                case NOBODY: cp.getSettings().setPartyInvites(PlayerSettings.Sender.ALL); break;
            }

            cp.updateSettings();
            new PlayerSettingsInventory(p);
        });

        setItem(InventorySlot.ROW_4_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
            new ProfileInventory(p);
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
        });

        openInventory();
    }

    private ItemBuilder getSenderItem(PlayerSettings.Sender sender) {
        switch (sender) {
            case ALL: return new ItemBuilder(Material.INK_SACK, 1, 10).displayName("§a§lVon Jedem");
            case FRIENDS: return new ItemBuilder(Material.INK_SACK, 1, 14).displayName("§e§lVon Freunden");
            case NOBODY: return new ItemBuilder(Material.INK_SACK, 1, 1).displayName("§c§lVon Niemandem");
            default: return new ItemBuilder(Material.INK_SACK);
        }
    }

}
