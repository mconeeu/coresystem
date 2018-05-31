/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.channel;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.channel.FutureTask;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChannelHandler implements eu.mcone.coresystem.api.bukkit.channel.ChannelHandler {

    public void sendPluginMessage(Player p, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("mc1main");
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(BukkitCoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendPluginMessage(Player p, FutureTask<String> task, String... write) {
        PluginChannelListener.tasks.put(p.getUniqueId(), task);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("mc1main");
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(BukkitCoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void sendPluginMessage(String... write) {
        CoreSystem.getInstance().sendConsoleMessage("sending plugin message "+write[0]);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("mc1main");
        for (String w : write) {
            out.writeUTF(w);
        }

        Bukkit.getServer().sendPluginMessage(BukkitCoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

}
