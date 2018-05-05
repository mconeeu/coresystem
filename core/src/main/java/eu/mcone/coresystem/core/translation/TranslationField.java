/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.translation;

import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.Translation;

import java.util.HashMap;
import java.util.Map;

public class TranslationField implements Translation {

    private final Map<Language, String> values;

    public TranslationField(Map<Language, String> values) {
        this.values = values;
    }

    public TranslationField(String... translations) {
        this.values = new HashMap<>();

        int i = 0;
        for (Language language : Language.values()) {
            values.put(language, translations[i]);
        }
    }

    public String[] getTranslations() {
        return (String[]) values.values().toArray();
    }

    public String getString(Language language) {
        return values.getOrDefault(language, null);
    }

    public int getInt(Language language) {
        return Integer.valueOf(values.get(language));
    }

    public boolean getBoolean(Language language) {
        return Boolean.valueOf(values.get(language));
    }

}
