/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core;

import eu.mcone.coresystem.api.core.mysql.MySQL;
import eu.mcone.coresystem.api.core.player.CoinsAPI;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.PermissionManager;
import eu.mcone.coresystem.api.core.player.PlayerUtils;
import eu.mcone.coresystem.api.core.translation.TranslationManager;
import eu.mcone.coresystem.api.core.util.CooldownSystem;

import java.util.UUID;

public interface GlobalCoreSystem {

    /**
     * returns the mc1data database
     * @return mc1data database
     */
    MySQL getMySQL();

    /**
     * returns the BCS TranslationManager
     * @return TranslationManager instance
     */
    TranslationManager getTranslationManager();

    /**
     * returns the BCS PermissionManager
     * @return PermissionManager instance
     */
    PermissionManager getPermissionManager();

    /**
     * returns the BCS PlayerUtils
     * @return PlayerUtils instance
     */
    PlayerUtils getPlayerUtils();

    /**
     * runs a specific Task async
     * @param runnable
     */
    void runAsync(Runnable runnable);

    /**
     * returns the BCS CoinsAPI
     * @return CoinsAPI instance
     */
    CoinsAPI getCoinsAPI();

    /**
     * returns the BCS CooldownSystem
     * @return CooldownSystem instance
     */
    CooldownSystem getCooldownSystem();

    /**
     * returns the GlobalCorePlayer object by uuid
     * @param uuid uuid
     * @return GlobalCorePlayer
     */
    GlobalCorePlayer getGlobalCorePlayer(UUID uuid);

}
