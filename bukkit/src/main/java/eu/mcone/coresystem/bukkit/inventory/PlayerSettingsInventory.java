/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.settings.Setting;
import eu.mcone.coresystem.api.bukkit.inventory.settings.SettingsInventory;
import eu.mcone.coresystem.api.bukkit.inventory.settings.options.LanguageOption;
import eu.mcone.coresystem.api.bukkit.inventory.settings.options.SenderOption;
import eu.mcone.coresystem.api.bukkit.inventory.settings.settings.BooleanSetting;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerSettingsInventory extends SettingsInventory {

    public static final String TITLE = "§8» §c§lEinstellungen";

    PlayerSettingsInventory(Player p) {
        super(TITLE, p, e -> new ProfileInventory(CoreSystem.getInstance().getCorePlayer(p)));
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);
        PlayerSettings settings = cp.getSettings();

        addSetting(new BooleanSetting(new ItemBuilder(Material.SKULL_ITEM, 1, 3).displayName("§f§lErhalte Freundschaftsanfragen").create(), "Freundschaftsanfragen")
                .onChoose((player, result) -> {
                    settings.setEnableFriendRequests(result);
                    cp.updateSettings(settings);
                })
                .optionFinder(player -> settings.isEnableFriendRequests())
                .setEnabledDescription("§7§oKlicke zum akzeptieren von", "§7§oFreundschaftsanfragen")
                .setDisabledDescription("§7§oKlicke zum ablehnen von", "§7§oFreundschaftsanfragen")
                .create());

        addSetting(new Setting<>(
                Skull.fromUrl("http://textures.minecraft.net/texture/6f74f58f541342393b3b16787dd051dfacec8cb5cd3229c61e5f73d63947ad").toItemBuilder().displayName("§f§lSprache").create(),
                LanguageOption.LANGUAGE_OPTIONS)
                .chooseListener((player, result) -> {
                    settings.setLanguage(result.getLanguage());
                    cp.updateSettings(settings);
                })
                .currentOptionFinder(player -> LanguageOption.get(settings.getLanguage())));

        if (p.hasPermission("system.bungee.nick")) {
            addSetting(new BooleanSetting(new ItemBuilder(Material.NAME_TAG).displayName("§f§lAutomatisch Nicken").create(), "Automatischem Nicken")
                    .onChoose((player, result) -> {
                        settings.setAutoNick(result);
                        cp.updateSettings(settings);
                    })
                    .optionFinder(player -> settings.isAutoNick())
                    .setEnabledDescription("§7§oKlicke, um nicht mehr automatisch", "§7§ogenickt zu werden")
                    .setDisabledDescription("§7§oKlicke, um automatisch genickt", "§7§ozu werden")
                    .create());
        }

        SenderOption[] privateMsgSenders = SenderOption.makeSenderOptions("Private Nachrichten");
        addSetting(new Setting<>(
                new ItemBuilder(Material.PAPER)
                        .displayName("§f§lPrivate Nachrichten erhalten")
                        .lore("§7§oKlicke um auszuwählen von wem", "§7§odu private Nachrichten erhalten", "§7möchtest.")
                        .create(),
                privateMsgSenders)
                .chooseListener((player, result) -> {
                    settings.setPrivateMessages(result.getSender());
                    cp.updateSettings(settings);
                })
                .currentOptionFinder(player -> SenderOption.get(settings.getPrivateMessages(), privateMsgSenders)));

        SenderOption[] partySenders = SenderOption.makeSenderOptions("Partyanfragen");
        addSetting(new Setting<>(
                new ItemBuilder(Material.CAKE)
                        .displayName("§f§lPartyanfragenanfragen erhalten")
                        .lore("§7§oWähle aus von wem du", "§7§oPartyanfragen erhalten", "§7möchtest.")
                        .create(),
                partySenders)
                .chooseListener((player, result) -> {
                    settings.setPartyInvites(result.getSender());
                    cp.updateSettings(settings);
                })
                .currentOptionFinder(player -> SenderOption.get(settings.getPartyInvites(), partySenders)));

        SenderOption[] joinMeSenders = SenderOption.makeSenderOptions("JoinMe Nachrichten");
        addSetting(new Setting<>(
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .displayName("§f§lJoinMe Benachrichtigungen erhalten")
                        .lore("§7§oWähle aus von wem du", "§7§oJoinMe Benachrichtigungen erhalten", "§7möchtest.")
                        .create(),
                joinMeSenders)
                .chooseListener((player, result) -> {
                    settings.setJoinMeMessages(result.getSender());
                    cp.updateSettings(settings);
                })
                .currentOptionFinder(player -> SenderOption.get(settings.getJoinMeMessages(), joinMeSenders)));

        addSetting(new BooleanSetting(new ItemBuilder(Material.NOTE_BLOCK).displayName("§f§lSounds abspielen").create(), "Sounds abspielen")
                .onChoose((player, result) -> {
                    settings.setPlaySounds(result);
                    cp.updateSettings(settings);
                })
                .optionFinder(player -> settings.isPlaySounds())
                .setEnabledDescription("§7§oKlicke, um keine Sounds mehr", "§7§ozu hören")
                .setDisabledDescription("§7§oKlicke, Sounds wieder zu", "§7§ohören")
                .create());

        openInventory();
    }

}
