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
import java.util.ArrayList;
import java.util.List;

public class BungeeCordReturnPluginChannelListener implements PluginMessageListener {

    static List<FutureTask<DataInputStream>> tasks = new ArrayList<>();

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        if (tasks.size() > 0) {
            tasks.get(tasks.size()-1).execute(in);
            tasks.remove(tasks.size()-1);
        }
    }

}
