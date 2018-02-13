/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.channel;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PluginMessage {

    public PluginMessage(Player p, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("mc1main");
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendPluginMessage(CoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

    public PluginMessage(String... write) {
        System.out.println("sending plugin message "+write[0]);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("mc1main");
        for (String w : write) {
            out.writeUTF(w);
        }

        Bukkit.getServer().sendPluginMessage(CoreSystem.getInstance(), "BungeeCord", out.toByteArray());
    }

}
