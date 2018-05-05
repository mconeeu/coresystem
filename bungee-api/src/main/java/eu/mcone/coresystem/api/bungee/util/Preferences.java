/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.util;

public interface Preferences {

    void reload();

    void setPreference(Preference key, String value);

    String getLiveString(Preference preference);

    int getLiveInt(Preference preference);

    boolean getLiveBoolean(Preference preference);

    String getString(Preference preference);

    int getInt(Preference preference);

    boolean getBoolean(Preference preference);

}
