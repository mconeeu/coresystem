/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.channel;

import org.bukkit.entity.Player;

public interface ChannelHandler {

    /**
     * sends a plugin message over the mc one plugin messaging channel
     * @param player target player
     * @param write message array
     */
    void sendPluginMessage(Player player, String... write);

    /**
     * sends a plugin message with a specific task over the mc one plugin messaging channel
     * @param player target player
     * @param task task to do after
     * @param write message array
     */
    void sendPluginMessage(Player player, FutureTask<String> task, String... write);

    /**
     * sends a plugin message via random player over the mc one plugin messaging channel
     * @param write message array
     */
    void sendPluginMessage(String... write);

}
