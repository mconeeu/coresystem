/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class ChannelHandler implements eu.mcone.coresystem.api.bungee.util.ChannelHandler {

    @Override
    public void createReturnRequest(Server server, String uuid, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid);
        for (String w : write) {
            out.writeUTF(w);
        }

        server.getInfo().sendData("mcone:return", out.toByteArray());
    }

    @Override
    public void createInfoRequest(ProxiedPlayer player, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getUniqueId().toString());
        for (String w : write) {
            out.writeUTF(w);
        }

        player.getServer().getInfo().sendData("mcone:info", out.toByteArray());
    }

    @Override
    public void sendPluginMessage(Server server, String channel, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String w : write) {
            out.writeUTF(w);
        }

        server.getInfo().sendData(channel, out.toByteArray());
    }

    @Override
    public void sendPluginMessage(ProxiedPlayer p, String channel, String... write) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String w : write) {
            out.writeUTF(w);
        }

        p.sendData(channel, out.toByteArray());
    }

}
