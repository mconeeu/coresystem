/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.PermissionManager;
import eu.mcone.coresystem.api.core.player.PlayerUtils;
import eu.mcone.coresystem.api.core.translation.TranslationManager;
import eu.mcone.coresystem.api.core.util.CooldownSystem;

import java.util.UUID;

public interface GlobalCoreSystem {

    /**
     * returns the mc1data MongoDB database
     *
     * @return mc1data MongoDB database
     */
    MongoDatabase getMongoDB();

    /**
     * returns the mc1stats MongoDB database
     *
     * @return mc1stats MongoDB database
     */
    MongoDatabase getStatsDB();

    /**
     * returns the CoreSystems instance of Gson. Use this for better performance
     *
     * @return gson instance
     */
    Gson getGson();

    /**
     * returns the CoreSystems instance of JsonParser. Use this for better performance
     *
     * @return JsonParser instance
     */
    JsonParser getJsonParser();

    /**
     * returns the BCS TranslationManager
     *
     * @return TranslationManager instance
     */
    TranslationManager getTranslationManager();

    /**
     * returns the BCS PermissionManager
     *
     * @return PermissionManager instance
     */
    PermissionManager getPermissionManager();

    /**
     * returns the BCS PlayerUtils
     *
     * @return PlayerUtils instance
     */
    PlayerUtils getPlayerUtils();

    /**
     * runs a specific Task async
     *
     * @param runnable
     */
    void runAsync(Runnable runnable);

    /**
     * returns the BCS CooldownSystem
     *
     * @return CooldownSystem instance
     */
    CooldownSystem getCooldownSystem();

    /**
     * returns the GlobalCorePlayer object by uuid
     *
     * @param uuid uuid
     * @return GlobalCorePlayer
     */
    GlobalCorePlayer getGlobalCorePlayer(UUID uuid);

}
