/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.translation;

import eu.mcone.coresystem.api.core.chat.MarkdownParser;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;

import java.util.Collection;
import java.util.Map;

public final class TranslationField {

    private final Map<Language, String> values;

    public TranslationField(Map<Language, String> values, TextLevel level) {
        values.replaceAll((k, v) -> MarkdownParser.parseMarkdown(v, level));
        this.values = values;
    }

    public void setTranslation(Language language, String value) {
        values.put(language, value);
    }

    /**
     * get all translation values as list. ordered like the language enum
     *
     * @return translation list
     */
    public Collection<String> getTranslations() {
        return values.values();
    }

    /**
     * returns the translation for a specific language
     *
     * @param language language
     * @return translation
     */
    public String getString(Language language) {
        return values.getOrDefault(language, null);
    }

    /**
     * returns the translation for a specific language as Integer
     *
     * @param language language
     * @return translation
     */
    public int getInt(Language language) {
        return Integer.valueOf(values.get(language));
    }

    /**
     * returns the translation for a specific language as Boolean
     *
     * @param language language
     * @return translation
     */
    public boolean getBoolean(Language language) {
        return Boolean.valueOf(values.get(language));
    }

    @Override
    public String toString() {
        return "TranslationField{" +
                "values=" + values +
                '}';
    }
}
