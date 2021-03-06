/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;

import java.util.UUID;

public interface PlayerUtils {

    /**
     * Returns the skin info of a player with the specified UUID
     *
     * @param uuid uuid
     * @return skin
     */
    SkinInfo getSkinInfo(UUID uuid);

    /**
     * Returns the skin info of a player with the specified name
     *
     * @param name name
     * @return skin
     */
    SkinInfo getSkinInfo(String name);

    /**
     * construct new SkinInfo with in database stored texture
     *
     * @param databaseName packets texture name
     * @return new SkinInfo object
     */
    SkinInfo getSkinFromSkinDatabase(String databaseName) throws SkinNotFoundException;

    /**
     * fetch uuid from name, first method is getting from database
     *
     * @param name name
     * @return uuid
     */
    UUID fetchUuid(String name);

    /**
     * fetch uuid from name with mojang api
     *
     * @param name name
     * @return uuid
     */
    UUID fetchUuidFromMojangAPI(String name);

    /**
     * fetch name from uuid, first method is getting from database
     *
     * @param uuid uuid
     * @return name
     */
    String fetchName(UUID uuid);

    /**
     * fetch uuid from name with mojang api
     *
     * @param uuid uuid
     * @return name
     */
    String fetchNameFromMojangAPI(UUID uuid);

    /**
     * cunstruct new SkinInfo
     *
     * @param name      packets name
     * @param value     mojang-value
     * @param signature mojang-signature
     * @return new SkinInfo object
     */
    SkinInfo constructSkinInfo(String name, String value, String signature);

}
