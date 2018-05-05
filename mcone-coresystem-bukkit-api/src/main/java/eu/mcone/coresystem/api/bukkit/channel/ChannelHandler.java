/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.channel;

import org.bukkit.entity.Player;

public interface ChannelHandler {

    void sendPluginMessage(Player player, String... write);

    void sendPluginMessage(Player player, FutureTask<String> task, String... write);

    void sendPluginMessage(String... write);

}
