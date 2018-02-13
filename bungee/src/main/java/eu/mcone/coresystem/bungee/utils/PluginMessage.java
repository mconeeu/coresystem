/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils;

import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PluginMessage {

    public PluginMessage(String channel, ServerInfo server, String... message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            for (String msg : Arrays.asList(message)) {
                out.writeUTF(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData(channel, stream.toByteArray());
    }

}
