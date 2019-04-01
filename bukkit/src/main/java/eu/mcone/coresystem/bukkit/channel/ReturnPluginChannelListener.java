/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.channel.FutureTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReturnPluginChannelListener implements PluginMessageListener {

    static Map<String, FutureTask<String>> tasks = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            String uuid = in.readUTF();

            if (tasks.containsKey(uuid)) {
                tasks.get(uuid).execute(in.readUTF());
                tasks.remove(uuid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
