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

    MySQL getMySQL();

    TranslationManager getTranslationManager();

    PermissionManager getPermissionManager();

    PlayerUtils getPlayerUtils();

    void runAsync(Runnable runnable);

    CoinsAPI getCoinsAPI();

    CooldownSystem getCooldownSystem();

    GlobalCorePlayer getGlobalCorePlayer(UUID uuid);

}
