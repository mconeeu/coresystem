/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.translation.Language;
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

        setItem(InventorySlot.ROW_2_SLOT_4, Skull.fromUrl("http://textures.minecraft.net/texture/6f74f58f541342393b3b16787dd051dfacec8cb5cd3229c61e5f73d63947ad", 1).toItemBuilder().displayName("§f§lSprache").create());
        setItem(InventorySlot.ROW_3_SLOT_4, Skull.fromUrl(cp.getSettings().getLanguage().getTextureUrl(), 1).toItemBuilder().displayName("§f§l" + cp.getSettings().getLanguage().getName()).lore("§7§oKlicke zum ändern").create(), e -> {
            switch (cp.getSettings().getLanguage()) {
                case ENGLISH:
                    cp.getSettings().setLanguage(Language.GERMAN);
                    break;
                case GERMAN:
                    cp.getSettings().setLanguage(Language.FRENCH);
                    break;
                case FRENCH:
                    cp.getSettings().setLanguage(Language.ENGLISH);
                    break;
            }

            cp.updateSettings();
            new PlayerSettingsInventory(p);
        });

        setItem(InventorySlot.ROW_2_SLOT_6, new ItemBuilder(Material.PAPER).displayName("§f§lPrivate Nachrichten erhalten").create());
        setItem(InventorySlot.ROW_3_SLOT_6, getSenderItem(cp.getSettings().getPrivateMessages()).lore("§7§oKlicke um auszuwählen von wem", "§7§odu private Nachrichten erhalten", "§7möchtest.").create(), e -> {
            switch (cp.getSettings().getPrivateMessages()) {
                case ALL:
                    cp.getSettings().setPrivateMessages(PlayerSettings.Sender.FRIENDS);
                    break;
                case FRIENDS:
                    cp.getSettings().setPrivateMessages(PlayerSettings.Sender.NOBODY);
                    break;
                case NOBODY:
                    cp.getSettings().setPrivateMessages(PlayerSettings.Sender.ALL);
                    break;
            }

            cp.updateSettings();
            new PlayerSettingsInventory(p);
        });

        setItem(InventorySlot.ROW_2_SLOT_7, new ItemBuilder(Material.CAKE).displayName("§f§lPartyanfragenanfragen erhalten").create());
        setItem(InventorySlot.ROW_3_SLOT_7, getSenderItem(cp.getSettings().getPartyInvites()).lore("§7§oKlicke um auszuwählen von wem", "§7§odu Partyanfragen erhalten", "§7möchtest.").create(), e -> {
            switch (cp.getSettings().getPartyInvites()) {
                case ALL:
                    cp.getSettings().setPartyInvites(PlayerSettings.Sender.FRIENDS);
                    break;
                case FRIENDS:
                    cp.getSettings().setPartyInvites(PlayerSettings.Sender.NOBODY);
                    break;
                case NOBODY:
                    cp.getSettings().setPartyInvites(PlayerSettings.Sender.ALL);
                    break;
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
            case ALL:
                return new ItemBuilder(Material.INK_SACK, 1, 10).displayName("§a§lVon Jedem");
            case FRIENDS:
                return new ItemBuilder(Material.INK_SACK, 1, 14).displayName("§e§lVon Freunden");
            case NOBODY:
                return new ItemBuilder(Material.INK_SACK, 1, 1).displayName("§c§lVon Niemandem");
            default:
                return new ItemBuilder(Material.INK_SACK);
        }
    }

}
