/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import java.util.UUID;

public interface PlayerUtils {

    /**
     * Returns the skin info of a player with the specified UUID, the UUID can also be passed through the fetchUuid method.
     * @param name
     * @param category
     * @return
     */
    SkinInfo getSkinInfo(String name, String category);

    /**
     * Uploads the Skin Info from the Player.
     * @param uuid Player uuid
     * @param category Texture Category
     */
    void uploadSkinInfo(UUID uuid, String category);

    /**
     * fetch uuid from name, first method is getting from database
     * @param name name
     * @return uuid
     */
    UUID fetchUuid(String name);

    /**
     * fetch uuid from name with mojang api
     * @param name name
     * @return uuid
     */
    UUID fetchUuidFromMojangAPI(String name);

    /**
     * fetch name from uuid, first method is getting from database
     * @param uuid uuid
     * @return name
     */
    String fetchName(UUID uuid);

    /**
     * fetch uuid from name with mojang api
     * @param uuid uuid
     * @return name
     */
    String fetchNameFromMojangAPI(UUID uuid);

    /**
     * cunstruct new SkinInfo
     * @param name data name
     * @param value mojang-value
     * @param signature mojang-signature
     * @return new SkinInfo object
     */
    SkinInfo constructSkinInfo(String name, String value, String signature);

    /**
     * construct new SkinInfo with in database stored texture
     * @param databaseName data texture name
     * @return new SkinInfo object
     */
    SkinInfo constructSkinInfo(String databaseName);
    
}
