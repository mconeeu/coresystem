/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.exception.CoreException;

public interface SkinInfo {

    SkinInfo downloadSkinData() throws CoreException;

    String getName();

    String getValue();

    String getSignature();

}
