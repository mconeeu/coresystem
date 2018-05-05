/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.util;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;

import java.util.UUID;

public interface CooldownSystem {

    boolean addAndCheck(GlobalCoreSystem instance, Class<?> clazz, UUID uuid);

    void addPlayer(UUID uuid, Class<?> clazz);

    boolean canExecute(GlobalCoreSystem instance, Class<?> clazz, UUID uuid);

}
