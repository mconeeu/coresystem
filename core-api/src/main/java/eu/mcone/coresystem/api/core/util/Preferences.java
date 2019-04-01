/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

public interface Preferences {

    void reload();

    void setPreference(String key, Object value);

    <T> T get(String preference, Class<T> type);

    <T> T getLive(String preference, Class<T> type);

}
