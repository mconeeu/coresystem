/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.mysql.MySQL;

import java.util.UUID;

public interface PlayerUtils {
    
    UUID fetchUuid(String name);
    
    String fetchName(UUID uuid);

    SkinInfo constructSkinInfo(String name, String value, String signature);

    SkinInfo constructSkinInfo(MySQL mySQL, String databaseName);
    
}
