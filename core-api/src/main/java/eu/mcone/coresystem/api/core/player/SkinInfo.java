/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.exception.CoreException;

public interface SkinInfo {

    /**
     * downloads skin data from database by predefined texture name
     * @return this
     * @throws CoreException thrown if texture name can not be found in database
     */
    SkinInfo downloadSkinData() throws CoreException;

    /**
     * Loads the skin data passed in the class constructor into the mc1 database
     * @throws CoreException thrown if texture name already exists in the database
     */
    void uploadSkindata() throws CoreException;

    /**
     * get the skins texture database name, null if not set
     * @return texture database name
     */
    String getName();

    /**
     * get the skins mojang-value
     * @return mojang-value
     */
    String getValue();

    /**
     * get the skins mojang-signature
     * @return mojang signature
     */
    String getSignature();

}
