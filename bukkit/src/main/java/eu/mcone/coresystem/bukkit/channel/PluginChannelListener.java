/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.channel.FutureTask;
import eu.mcone.coresystem.api.bukkit.event.CoinsChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PluginChannelListener implements PluginMessageListener {

    static Map<UUID, FutureTask<String>> tasks = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String s, Player p, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            String subchannel = in.readUTF();

            switch (subchannel) {
                case "FRIENDS": {
                    String input = in.readUTF();

                    if (input.equalsIgnoreCase("friends")) {
                        Player t = Bukkit.getPlayer(UUID.fromString(in.readUTF()));

                        if (t != null) {
                            String result = in.readUTF();
                            tasks.get(t.getUniqueId()).execute(result);
                        }
                    }
                    break;
                }
                case "PARTY": {
                    String input = in.readUTF();

                    if (input.equalsIgnoreCase("member")) {
                        Player t = Bukkit.getPlayer(UUID.fromString(in.readUTF()));

                        if (t != null) {
                            String result = in.readUTF();
                            tasks.get(t.getUniqueId()).execute(result);
                        }
                    }
                    break;
                }
                case "EVENT": {
                    BukkitCorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(UUID.fromString(in.readUTF()));
                    String event = in.readUTF();
                    String data = in.readUTF();

                    if (cp != null) {
                        if (event.equals("CoinsChangeEvent")) {
                            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(cp));
                        } else if (event.equals("PermissionChangeEvent")) {
                            Bukkit.getPluginManager().callEvent(new PermissionChangeEvent(cp, data.split(";")));
                        }
                    }
                    break;
                }
                case "COINS": {
                    BukkitCorePlayer cp = BukkitCoreSystem.getInstance().getCorePlayer(UUID.fromString(in.readUTF()));
                    Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(cp));
                    break;
                }
                case "NICK": {
                    UUID uuid = UUID.fromString(in.readUTF());
                    String nickname = in.readUTF();
                    String value = in.readUTF();
                    String signature = in.readUTF();

                    Player player = Bukkit.getPlayer(uuid);
                    BukkitCoreSystem.getInstance().getNickManager().nick(player, nickname, value, signature);
                    break;
                }
                case "UNNICK": {
                    UUID uuid = UUID.fromString(in.readUTF());

                    Player player = Bukkit.getPlayer(uuid);
                    BukkitCoreSystem.getInstance().getNickManager().unnick(player);
                    break;
                }
                case "SERVERS": {
                    String input = in.readUTF();

                    if (input.equalsIgnoreCase("list")) {
                        Player t = Bukkit.getPlayer(UUID.fromString(in.readUTF()));

                        if (t != null) {
                            String result = in.readUTF();
                            tasks.get(t.getUniqueId()).execute(result);
                        }
                    }
                    break;
                }
                case "PLAYER_SETTINGS": {
                    UUID uuid = UUID.fromString(in.readUTF());

                    Bukkit.getPluginManager().callEvent(new PlayerSettingsChangeEvent(
                            CoreSystem.getInstance().getCorePlayer(uuid),
                            CoreSystem.getInstance().getGson().fromJson(in.readUTF(), PlayerSettings.class)
                    ));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
