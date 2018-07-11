/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.channel;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.mcone.coresystem.api.bukkit.channel.FutureTask;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.util.UUID;

public class ChannelHandler implements eu.mcone.coresystem.api.bukkit.channel.ChannelHandler {

    @Override
    public void createGetRequest(Player p, FutureTask<String> task, String... write) {
        String uuid = UUID.randomUUID().toString();
        ReturnPluginChannelListener.tasks.put(uuid, task);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MC_ONE_GET");
        out.writeUTF(uuid);
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(BukkitCoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void createBungeeGetRequest(Player p, FutureTask<DataInputStream> task, String... write) {
        BungeeCordReturnPluginChannelListener.tasks.add(task);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(BukkitCoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void createSetRequest(Player p, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MC_ONE_SET");
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(BukkitCoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void sendPluginMessage(Player p, String channel, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(BukkitCoreSystem.getInstance(), channel, out.toByteArray());
    }

    @Override
    public void sendPluginMessage(String channel, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String w : write) {
            out.writeUTF(w);
        }

        Bukkit.getServer().sendPluginMessage(BukkitCoreSystem.getInstance(), channel, out.toByteArray());
    }

}
