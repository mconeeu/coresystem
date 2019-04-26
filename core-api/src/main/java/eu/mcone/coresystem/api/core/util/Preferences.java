/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

public interface Preferences {

    /**
     * reloads the preferences from database
     */
    void reload();

    /**
     * sets a new preference or override an existing
     * @param key key
     * @param value value (use Primitives here)
     */
    void setPreference(String key, Object value);

    /**
     * returns the cached preference value of the given key with the given type
     * @param preference preference key
     * @param type return type
     * @return preference value
     */
    <T> T get(String preference, Class<T> type);

    /**
     * returns the live preference value of the given key with the given type
     * Attention! for this method a database call must be executed
     * @param preference preference key
     * @param type return type
     * @return preference value
     */
    <T> T getLive(String preference, Class<T> type);

}
