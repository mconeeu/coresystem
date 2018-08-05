/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core;

import com.google.gson.Gson;
import eu.mcone.coresystem.core.mongoDB.MongoDBManager;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.mysql.MySQL;

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

    MySQL getMySQL(Database database);

    MongoDBManager getMongoDBManager();

    Gson getGson();

    Gson getSimpleGson();

}
