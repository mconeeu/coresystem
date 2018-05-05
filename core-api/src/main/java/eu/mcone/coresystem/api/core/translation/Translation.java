/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.translation;

public interface Translation {

    String[] getTranslations();

    String getString(Language language);

    int getInt(Language language);

    boolean getBoolean(Language language);

}
