/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.translation;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import eu.mcone.coresystem.api.core.chat.MarkdownParser;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.include;

public class TranslationManager implements eu.mcone.coresystem.api.core.translation.TranslationManager {

    private static final List<Language> DEFAULT_LANGUAGES = new ArrayList<>(Collections.singleton(Language.GERMAN));

    private final CoreModuleCoreSystem system;
    private final MongoCollection<Document> collection;

    private final Map<String, TranslationField> translations;

    @Getter
    private final List<String> loadedCategories;
    @Getter
    private final List<Language> loadedLanguages;

    public TranslationManager(CoreModuleCoreSystem system, String... categories) {
        this.system = system;
        this.collection = system.getMongoDB(Database.SYSTEM).getCollection("translations");
        this.translations = new HashMap<>();
        this.loadedCategories = new ArrayList<>(Collections.singleton("undefined"));
        this.loadedCategories.addAll(Arrays.asList(categories));
        this.loadedLanguages = new ArrayList<>(DEFAULT_LANGUAGES);

        StringBuilder sb = new StringBuilder("§2Loading Translation categories in default languages:");
        for (String category : categories) {
            sb.append(" ").append(category);
        }
        system.sendConsoleMessage(sb.toString());

        reload();
    }

    @Override
    public void reload() {
        translations.clear();

        for (Document document : collection
                .find(in("category", loadedCategories.toArray(new String[0])))
                .projection(include(getQueryCols(loadedLanguages)))
        ) {
            Map<Language, String> values = new HashMap<>();
            for (Language language : Language.values()) {
                if (document.get(language.getId()) != null) {
                    values.put(language, document.getString(language.getId()));
                }
            }

            setTranslation(
                    document.getString("key"),
                    document.getString("level") != null ? TextLevel.valueOf(document.getString("level")) : TextLevel.NONE,
                    values
            );
        }
    }

    @Override
    public void registerKeys(String category, List<String> keys) {
        List<Document> keyDocuments = new ArrayList<>();
        for (String key : keys) {
            keyDocuments.add(
                    new Document("key", key).append("category", category)
            );
        }

        try {
            collection.insertMany(keyDocuments, new InsertManyOptions().ordered(false));
        } catch (MongoBulkWriteException ignored) {
        }
    }

    @Override
    public void loadAdditionalCategories(String... categories) {
        List<String> categoryList = new ArrayList<>();
        for (String category : categories) {
            if (!loadedCategories.contains(category)) {
                loadedCategories.add(category);
                categoryList.add(category);
            }
        }

        if (!categoryList.isEmpty()) {
            StringBuilder sb = new StringBuilder("§2Loading Translation categories:");
            for (String category : categoryList) {
                sb.append(" ").append(category);
            }
            system.sendConsoleMessage(sb.toString());

            for (Document document : collection
                    .find(in("category", categoryList.toArray(new String[0])))
                    .projection(include(getQueryCols(loadedLanguages)))
            ) {
                Map<Language, String> values = new HashMap<>();
                for (Language language : Language.values()) {
                    if (document.get(language.getId()) != null) {
                        values.put(language, document.getString(language.getId()));
                    }
                }

                setTranslation(
                        document.getString("key"),
                        document.getString("level") != null ? TextLevel.valueOf(document.getString("level")) : TextLevel.NONE,
                        values
                );
            }
        }
    }

    @Override
    public void loadAdditionalLanguages(Language... languages) {
        List<Language> languageList = new ArrayList<>();
        for (Language language : languages) {
            if (!loadedLanguages.contains(language)) {
                loadedLanguages.add(language);
                languageList.add(language);
            }
        }

        if (!languageList.isEmpty()) {
            StringBuilder sb = new StringBuilder("§2Loading Translation languages:");
            for (Language language : languageList) {
                sb.append(" ").append(language.getName());
            }
            system.sendConsoleMessage(sb.toString());

            for (Document document : collection
                    .find(in("category", loadedCategories.toArray(new String[0])))
                    .projection(include(getQueryCols(languageList)))
            ) {
                Map<Language, String> values = new HashMap<>();
                for (Language language : Language.values()) {
                    if (document.get(language.getId()) != null) {
                        values.put(language, document.getString(language.getId()));
                    }
                }

                setTranslation(
                        document.getString("key"),
                        document.getString("level") != null ? TextLevel.valueOf(document.getString("level")) : TextLevel.NONE,
                        values
                );
            }
        }
    }

    private void setTranslation(String key, TextLevel level, Map<Language, String> translations) {
        if (this.translations.containsKey(key)) {
            TranslationField translationField = this.translations.get(key);

            for (Map.Entry<Language, String> translation : translations.entrySet()) {
                translationField.setTranslation(translation.getKey(), MarkdownParser.parseMarkdown(translation.getValue(), level));
            }

        } else {
            this.translations.put(key, new TranslationField(translations, level));
        }
    }

    @Override
    public TranslationField getTranslations(String key) {
        return translations.getOrDefault(key, null);
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

            if (result == null && !language.equals(Language.ENGLISH)) {
                result = translations.get(key).getString(Language.ENGLISH);
            }
            if (result == null && !language.equals(Language.GERMAN)) {
                result = translations.get(key).getString(Language.GERMAN);
            }

            if (result != null) {
                return result.replaceAll("&", "§");
            }
        }

        return key;
    }

    private String[] getQueryCols(List<Language> languages) {
        String[] cols = new String[languages.size() + 3];

        int i = 0;
        cols[i++] = "key";
        cols[i++] = "level";
        cols[i++] = "category";
        for (Language language : languages) {
            cols[i++] = language.getId();
        }

        return cols;
    }

}
