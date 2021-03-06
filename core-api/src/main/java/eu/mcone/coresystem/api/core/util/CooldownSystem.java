/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

import java.util.UUID;

public interface CooldownSystem {

    int DEFAULT_COOLDOWN = 3;

    /**
     * adds the current access to list and checks if there already is an list entry
     *
     * @param clazz    target class
     * @param uuid     player uuid
     * @return true if player is bypassing the cooldown and should be blocked
     */
    boolean addAndCheck(Class<?> clazz, UUID uuid);

    boolean addAndCheck(Class<?> clazz, UUID uuid, boolean notify);

    /**
     * adds a player to the list
     *
     * @param uuid  player uuid
     * @param clazz target class
     */
    void addPlayer(UUID uuid, Class<?> clazz);

    /**
     * sets a custom Cooldown time for the specific class
     *
     * @param clazz    target class
     * @param cooldown cooldown time in seconds
     */
    void setCustomCooldownFor(Class<?> clazz, int cooldown);

    /**
     * checks if a player is currently bypassing the cooldown or not
     *
     * @param clazz    target class
     * @param uuid     player uuid
     * @return true if player is bypassing the cooldown and should be blocked
     */
    boolean canExecute(Class<?> clazz, UUID uuid);

    boolean canExecute(Class<?> clazz, UUID uuid, boolean notify);
}
