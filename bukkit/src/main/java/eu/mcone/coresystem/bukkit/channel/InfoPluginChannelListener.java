/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.nick.UnnickEvent;
import eu.mcone.coresystem.api.bukkit.event.player.MoneyChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.nick.NickEvent;
import eu.mcone.coresystem.api.bukkit.event.player.PermissionChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.player.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class InfoPluginChannelListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            Player p = Bukkit.getPlayer(UUID.fromString(in.readUTF()));
            CorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(p);
            String subchannel = in.readUTF();

            switch (subchannel) {
                case "EVENT": {
                    String event = in.readUTF();
                    String data = in.readUTF();

                    if (cp != null) {
                        if (event.equals("MoneyChangeEvent")) {
                            Currency currency = Currency.valueOf(data);
                            int amount = Integer.parseInt(in.readUTF());

                            switch (currency) {
                                case COINS:
                                    ((GlobalOfflineCorePlayer) cp).setCoinsAmount(amount);
                                    break;
                                case EMERALDS:
                                    ((GlobalOfflineCorePlayer) cp).setEmeraldsAmount(amount);
                                    break;
                            }
                            Bukkit.getPluginManager().callEvent(new MoneyChangeEvent(cp, currency));
                        } else if (event.equals("PermissionChangeEvent")) {
                            Bukkit.getPluginManager().callEvent(new PermissionChangeEvent(cp, data.split(";")));
                        }
                    }
                    break;
                }
                case "NICK": {
                    Nick nick = new Nick(in.readUTF(), in.readUTF(), Group.valueOf(in.readUTF()), in.readUTF(), in.readUTF(), Integer.parseInt(in.readUTF()), Long.parseLong(in.readUTF()));
                    boolean notify = Boolean.parseBoolean(in.readUTF());
                    NickEvent event = new NickEvent(cp, CoreSystem.getInstance().getNickManager().isAllowSkinChange(), nick);
                    Bukkit.getPluginManager().callEvent(event);

                    if (!event.isCancelled())
                        BukkitCoreSystem.getInstance().getNickManager().nick(p, nick, notify);
                    break;
                }
                case "UNNICK": {
                    boolean bypassSkin = !CoreSystem.getInstance().getNickManager().isAllowSkinChange();
                    UnnickEvent event = new UnnickEvent(cp, bypassSkin);
                    Bukkit.getPluginManager().callEvent(event);

                    if (!event.isCancelled()) BukkitCoreSystem.getInstance().getNickManager().unnick(p, bypassSkin);
                    break;
                }
                case "PLAYER_SETTINGS": {
                    Bukkit.getPluginManager().callEvent(new PlayerSettingsChangeEvent(
                            CoreSystem.getInstance().getCorePlayer(p.getUniqueId()),
                            CoreSystem.getInstance().getGson().fromJson(in.readUTF(), PlayerSettings.class)
                    ));
                    break;
                }
                case "CMD": {
                    Bukkit.dispatchCommand(p, in.readUTF());
                    break;
                }
                case "INVENTORY": {
                    String action = in.readUTF();

                    if (action.equalsIgnoreCase("CLOSE")) {
                        p.closeInventory();
                    }
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
