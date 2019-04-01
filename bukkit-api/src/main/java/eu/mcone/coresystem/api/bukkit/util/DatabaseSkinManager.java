/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;

public interface DatabaseSkinManager {

    /**
     * construct new SkinInfo with in database stored texture
     * @param databaseName data texture name
     * @return new SkinInfo object
     */
    SkinInfo getSkin(String databaseName) throws SkinNotFoundException;

    /**
     * get Head from Database skin
     * @param databaseName data texture name
     * @param amount amount of items in iteemstack
     * @return new ItemBuilder
     */
    ItemBuilder getHead(String databaseName, int amount) throws SkinNotFoundException;

    /**
     * get Head from Database skin
     * @param databaseName data texture name
     * @return new ItemBuilder
     */
    ItemBuilder getHead(String databaseName) throws SkinNotFoundException;

}
