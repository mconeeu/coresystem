/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.translation;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;

import java.util.Map;

public interface TranslationManager {

    /**
     * reloads all translations from database
     */
    void reload();

    /**
     * get a specific translation
     * @param key database key
     * @return translation field
     */
    TranslationField getTranslations(String key);

    /**
     * gets a translation in the default language
     * @param key database key
     * @return translation
     */
    String get(String key);

    /**
     * gets a translation for a specific language
     * @param key database key
     * @param language language
     * @return translation
     */
    String get(String key, Language language);

    /**
     * gets a translation for the preferred language of a specific player
     * default language, if null
     * @param key database key
     * @param player player
     * @return translation
     */
    String get(String key, GlobalCorePlayer player);

    /**
     * insert predefined translations as Map
     * @param translations Map with key and TranslationField
     */
    void insertIfNotExists(Map<String, TranslationField> translations);

}
