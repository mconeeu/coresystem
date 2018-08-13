/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core;

import com.google.gson.Gson;
import eu.mcone.coresystem.core.mysql.MySQL;
import eu.mcone.coresystem.core.mysql.MySQLDatabase;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;

public interface CoreModuleCoreSystem {

    default boolean checkIfCloudSystemAvailable() {
        try {
            Class.forName("eu.mcone.cloud.plugin.CloudPlugin");
            return true;
        } catch (ClassNotFoundException e) {
           return false;
        }
    }

    void sendConsoleMessage(String message);

    MySQL getMySQL(MySQLDatabase database);

    MongoDatabase getMongoDB(Database database);

    Gson getGson();

    Gson getSimpleGson();

}
