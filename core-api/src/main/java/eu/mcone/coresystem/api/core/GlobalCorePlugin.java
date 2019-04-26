/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core;


public interface GlobalCorePlugin {

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
