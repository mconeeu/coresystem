/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.translation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import org.bson.Document;

import java.util.*;

public class TranslationManager implements eu.mcone.coresystem.api.core.translation.TranslationManager {

    private MongoCollection<Document> collection;
    private Map<String, TranslationField> translations;
    private List<String> categories;

    public TranslationManager(MongoDatabase database, GlobalCorePlugin coreSystem, String... categories) {
        this.collection = database.getCollection("translations");
        this.translations = new HashMap<>();
        this.categories = new ArrayList<>();

        this.categories.add(coreSystem.getPluginName());
        this.categories.addAll(Arrays.asList(categories));

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
                        translations.put(document.getString("key").replaceAll("&", "§"), new TranslationField(values));
                    }
                }
            } else {
                final Map<Language, String> values = new HashMap<>();
                for (Language language : Language.values()) {
                    values.put(language, document.getString(language.getId()));
                }
                translations.put(document.getString("key").replaceAll("&", "§"), new TranslationField(values));
            }
        }
    }

    @Override
    public void loadCategories(GlobalCorePlugin plugin, String... categories) {
        this.categories.add(plugin.getPluginName());
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
    public String get(String key, GlobalCorePlayer player) {
        return get(key, player.getSettings().getLanguage());
    }

    private String getTranslation(String key, Language language) {
        if (translations.containsKey(key)) {
            String result = translations.get(key).getString(language);

            if (result != null) {
                return result.replaceAll("&", "§");
            } else {
                result = translations.get(key).getString(Language.GERMAN);
                return result.replaceAll("&", "§");
            }
        } else {
            return null;
        }
    }

}
