/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.*;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.*;
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
                    SkinInfo info = CoreSystem.getInstance().getPlayerUtils() .constructSkinInfo(in.readUTF(), in.readUTF(), in.readUTF());
                    Nick nick = new Nick(in.readUTF(), info.getName(), Group.valueOf(in.readUTF()), in.readInt(), in.readInt());
                    nick.setSkinInfo(info);

                    if (CoreSystem.getInstance().getNickManager().isAllowSkinChange()) {
                        NickEvent event = new NickEvent(cp, true);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled())
                            BukkitCoreSystem.getInstance().getNickManager().nick(p, nick);
                    } else {
                        NickEvent event = new NickEvent(cp, false);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled())
                            BukkitCoreSystem.getInstance().getNickManager().nick(p, nick);
                    }
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
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
