/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.translation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

public class TranslationManager implements eu.mcone.coresystem.api.core.translation.TranslationManager {

    private MongoCollection<Document> collection;
    private Map<String, TranslationField> translations;
    private List<String> categories;
    private List<Language> languages;

    public TranslationManager(MongoDatabase database, Language... languages) {
        this.collection = database.getCollection("translations");
        this.translations = new HashMap<>();
        this.categories = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.languages.addAll(Arrays.asList(languages));

        reload();
    }

    @Override
    public void reload() {
        translations.clear();

        for (Document document : collection.find()) {
            if (document.getString("category") != null) {
                for (String cat : categories) {
                    if (document.getString("category").equalsIgnoreCase(cat)) {
                        final Map<Language, String> values = new HashMap<>();
                        for (Language language : Language.values()) {
                            values.put(language, document.getString(language.getId()));
                        }

                        translations.put(document.getString("key"), new TranslationField(values));
                    }
                }
            } else {
                final Map<Language, String> values = new HashMap<>();
                for (Language language : Language.values()) {
                    values.put(language, document.getString(language.getId()));
                }

                translations.put(document.getString("key"), new TranslationField(values));
            }
        }
    }

    @Override
    public void insertKeys(List<String> keys, String category) {
        for (String key : keys) {
            if (collection.find(combine(eq("key", key), eq("category", category))).first() == null) {
                collection.insertOne(
                        new Document("key", key)
                                .append("category", category)
                                .append(Language.ENGLISH.getId(), null)
                                .append(Language.GERMAN.getId(), null)
                                .append(Language.BAVARIA.getId(), null)
                );
            }
        }
    }

    @Override
    public void loadTranslation(GlobalCorePlayer player) {
        if (!languages.contains(player.getSettings().getLanguage())) {
            for (Document document : collection.find()) {
                String category = document.getString("category");
                if (category != null) {
                    if (categories.contains(category)) {
                        translations.put(document.getString("key"), new TranslationField(player.getSettings().getLanguage().getName()));
                    }
                }
            }
        }
    }

    @Override
    public void loadCategories(String... categories) {
//        this.categories.add(plugin.getPluginName());
        this.categories.addAll(Arrays.asList(categories));

        reload();
    }

    @Override
    public TranslationField getTranslations(String key) {
        return translations.get(key);
    }

    @Override
    public String get(String key) {
        return getTranslation(key, Language.ENGLISH);
    }

    @Override
    public String get(String key, Language language) {
        return getTranslation(key, language);
    }

    @Override
    public String get(String key, Language language, Object... replace) {
        return replace(getTranslation(key, language), replace);
    }


    @Override
    public String get(String key, GlobalCorePlayer player) {
        return get(key, player.getSettings().getLanguage());
    }

    @Override
    public String get(String key, GlobalCorePlayer player, Object... replace) {
        return replace(get(key, player.getSettings().getLanguage()), replace);
    }

    private String replace(String translation, Object... replace) {
        List<Object> replaces = Arrays.asList(replace);

        if (translation != null) {
            int i = 0;
            while (translation.contains("{" + i + "}") && replaces.get(i) != null) {
                translation = translation.replaceAll("\\{" + i + "}", String.valueOf(replaces.get(i)));
                i++;
            }
        }

        return translation;
    }

    private String getTranslation(String key, Language language) {
        if (translations.containsKey(key)) {
            String result = translations.get(key).getString(language);

            if (result != null) {
                return result.replaceAll("&", "§");
            } else {
                result = translations.get(key).getString(Language.ENGLISH);
                return result.replaceAll("&", "§");
            }
        } else {
            return null;
        }
    }
}
