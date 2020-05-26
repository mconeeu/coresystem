/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.core.util.MoneyUtil;
import eu.mcone.networkmanager.core.api.database.Database;

public interface CoreModuleCoreSystem {

    default boolean checkIfCloudSystemAvailable() {
        try {
            Class.forName("eu.mcone.cloud.plugin.CloudPlugin");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * sends a message to the console with the plugin name as prefix
     *
     * @param message message
     */
    void sendConsoleMessage(String message);

    MongoDatabase getMongoDB(Database database);

    Gson getGson();

    JsonParser getJsonParser();

    MoneyUtil getMoneyUtil();

}
