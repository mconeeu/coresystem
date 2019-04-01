/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.channel;

import org.bukkit.entity.Player;

import java.io.DataInputStream;

public interface ChannelHandler {

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     * @param player target player
     * @param task task to do after
     * @param write message array
     */
    void createGetRequest(Player player, FutureTask<String> task, String... write);

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     * @param player target player
     * @param task task to do after
     * @param write message array
     */
    void createBungeeGetRequest(Player player, FutureTask<DataInputStream> task, String... write);

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     * @param player target player
     * @param write message array
     */
    void createSetRequest(Player player, String... write);

    /**
     * sends a plugin message over the mc one plugin messaging channel
     * @param player target player
     * @param channel channel name (for ex. SET,...)
     * @param write message array
     */
    void sendPluginMessage(Player player, String channel, String... write);

    /**
     * sends a plugin message via random player over the mc one plugin messaging channel
     * @param channel channel name (for ex. SET,...)
     * @param write message array
     */
    void sendPluginMessage(String channel, String... write);

}
