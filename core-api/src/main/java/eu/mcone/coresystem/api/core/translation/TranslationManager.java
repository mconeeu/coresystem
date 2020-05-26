/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.translation;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;

import java.util.Collection;
import java.util.List;

public interface TranslationManager {

    Language DEFAULT_LANGUAGE = Language.ENGLISH;

    /**
     * reloads all translation fields from set categories from database
     */
    void reload();

    Collection<Language> getLoadedLanguages();

    Collection<String> getLoadedCategories();

    void registerKeys(String category, List<String> keys);

    void loadAdditionalLanguages(Language... languages);

    /**
     * get a specific translation
     *
     * @param key database key
     * @return translation field
     */
    TranslationField getTranslations(String key);

    /**
     * gets a translation in the default language
     *
     * @param key database key
     * @return translation
     */
    String get(String key);

    /**
     * gets a translation for a specific language
     *
     * @param key      database key
     * @param language language
     * @return translation
     */
    String get(String key, Language language);

    String get(String key, Language language, Object... replace);

    /**
     * gets a translation for the preferred language of a specific player
     * default language, if null
     *
     * @param key    database key
     * @param player player
     * @return translation
     */
    String get(String key, GlobalCorePlayer player);

    String get(String key, GlobalCorePlayer player, Object... replace);

    /**
     * loads all new Translations from a specific categories
     *
     * @param categories categories name
     */
    void loadAdditionalCategories(String... categories);

}
