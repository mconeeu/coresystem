/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.settings.Setting;
import eu.mcone.coresystem.api.bukkit.inventory.settings.SettingsInventory;
import eu.mcone.coresystem.api.bukkit.inventory.settings.options.LanguageOption;
import eu.mcone.coresystem.api.bukkit.inventory.settings.options.SenderOption;
import eu.mcone.coresystem.api.bukkit.inventory.settings.settings.BooleanSetting;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

class PlayerSettingsInventory extends SettingsInventory {

    PlayerSettingsInventory(Player p) {
        super("§8» §c§lEinstellungen", p);
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        addSetting(new BooleanSetting(new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§f§lErhalte Freundschaftsanfragen").create(), "Freundschaftsanfragen")
                .onChoose((player, result) -> {
                    cp.getSettings().setEnableFriendRequests(result);
                    cp.updateSettings();
                })
                .optionFinder(player -> cp.getSettings().isEnableFriendRequests())
                .setEnabledDescription("§7§oKlicke zum akzeptieren von", "§7§oFreundschaftsanfragen")
                .setDisabledDescription("§7§oKlicke zum ablehnen von", "§7§oFreundschaftsanfragen")
                .create());

        addSetting(new Setting<>(
                Skull.fromUrl("http://textures.minecraft.net/texture/6f74f58f541342393b3b16787dd051dfacec8cb5cd3229c61e5f73d63947ad").toItemBuilder().displayName("§f§lSprache").create(),
                LanguageOption.LANGUAGE_OPTIONS)
                .chooseListener((player, result) -> {
                    cp.getSettings().setLanguage(result.getLanguage());
                    cp.updateSettings();
                })
                .currentOptionFinder(player -> new LanguageOption(cp.getSettings().getLanguage())));

        if (p.hasPermission("system.bungee.nick")) {
            addSetting(new BooleanSetting(new ItemBuilder(Material.NAME_TAG).displayName("§f§lAutomatisch Nicken").create(), "Automatischem Nicken")
                    .onChoose((player, result) -> {
                        cp.getSettings().setAutoNick(result);
                        cp.updateSettings();
                    })
                    .optionFinder(player -> cp.getSettings().isAutoNick())
                    .setEnabledDescription("§7§oKlicke, um nicht mehr automatisch", "§7§ogenickt zu werden")
                    .setDisabledDescription("§7§oKlicke, um automatisch genickt", "§7§ozu werden")
                    .create());
        }

        addSetting(new Setting<>(
                new ItemBuilder(Material.PAPER)
                        .displayName("§f§lPrivate Nachrichten erhalten")
                        .lore("§7§oKlicke um auszuwählen von wem", "§7§odu private Nachrichten erhalten", "§7möchtest.")
                        .create(),
                SenderOption.makeSenderOptions("Private Nachrichten"))
                .chooseListener((player, result) -> {
                    cp.getSettings().setPrivateMessages(result.getSender());
                    cp.updateSettings();
                })
                .currentOptionFinder(player -> new SenderOption(cp.getSettings().getPrivateMessages(), "Private Nachrichten")));

        addSetting(new Setting<>(
                new ItemBuilder(Material.CAKE)
                        .displayName("§f§lPartyanfragenanfragen erhalten")
                        .lore("§7§oWähle aus von wem du", "§7§oPartyanfragen erhalten", "§7möchtest.")
                        .create(),
                SenderOption.makeSenderOptions("Partyanfragen"))
                .chooseListener((player, result) -> {
                    cp.getSettings().setPartyInvites(result.getSender());
                    cp.updateSettings();
                })
                .currentOptionFinder(player -> new SenderOption(cp.getSettings().getPartyInvites(), "Partyanfragen")));

        addSetting(new BooleanSetting(new ItemBuilder(Material.NOTE_BLOCK).displayName("§f§lSounds abspielen").create(), "Sounds abspielen")
                .onChoose((player, result) -> {
                    cp.getSettings().setPlaySounds(result);
                    cp.updateSettings();
                })
                .optionFinder(player -> cp.getSettings().isPlaySounds())
                .setEnabledDescription("§7§oKlicke, um keine Sounds mehr", "§7§ozu hören")
                .setDisabledDescription("§7§oKlicke, Sounds wieder zu", "§7§ohören")
                .create());
        
        setItem(InventorySlot.ROW_5_SLOT_9, BACK_ITEM, e -> {
            new ProfileInventory(CoreSystem.getInstance().getCorePlayer(p));
            Sound.error(p);
        });

        openInventory();
    }

}
