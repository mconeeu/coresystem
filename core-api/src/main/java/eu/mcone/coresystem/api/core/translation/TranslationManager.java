/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.translation;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;

import java.util.Map;

public interface TranslationManager {

    void reload();

    Translation getTranslations(String key);

    String get(String key);

    String get(String key, Language language);

    String get(String key, GlobalCorePlayer player);

    void insertIfNotExists(Map<String, Translation> translations);

}
