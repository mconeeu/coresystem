/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core;


import eu.mcone.coresystem.api.core.player.GlobalMessenger;

public interface GlobalCorePlugin {

    /**
     * returns the Messenger of the plugin
     * @return messenger instance
     */
    GlobalMessenger<?> getMessenger();

    /**
     * returns the plugin name
     * @return plugin name
     */
    String getPluginName();

    /**
     * sends a message to the console with the plugin name as prefix
     * @param message message
     */
    void sendConsoleMessage(String message);

}
