/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public interface ChannelHandler {

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     *
     * @param player target player
     * @param write  message array
     */
    void createInfoRequest(ProxiedPlayer player, String... write);

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     *
     * @param server target server
     * @param uuid   uuid
     * @param write  message array
     */
    void createReturnRequest(Server server, String uuid, String... write);

    /**
     * sends a plugin message over the mc one plugin messaging channel
     *
     * @param server  target server
     * @param channel channel name (for ex. SET,...)
     * @param write   message array
     */
    void sendPluginMessage(Server server, String channel, String... write);

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     *
     * @param player  target player
     * @param channel channel name  (for ex. SET,...)
     * @param write   message array
     */
    void sendPluginMessage(ProxiedPlayer player, String channel, String... write);

}
