/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.translation;

import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import org.bson.Document;

import java.util.*;

public class TranslationManager implements eu.mcone.coresystem.api.core.translation.TranslationManager {

    private MongoDatabase mongoDBManager;
    private Map<String, TranslationField> translations;
    private List<String> categories;

    public TranslationManager(MongoDatabase mongoDBManager, GlobalCorePlugin coreSystem, String... categories) {
        this.mongoDBManager = mongoDBManager;
        this.translations = new HashMap<>();
        this.categories = new ArrayList<>();

        this.categories.add(coreSystem.getPluginName());
        this.categories.addAll(Arrays.asList(categories));

        reload();
    }

    @Override
    public void reload() {
        translations.clear();
        for (Document document : mongoDBManager.getCollection("translations").find()) {
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

        /*
        StringBuilder qry = new StringBuilder("SELECT * FROM translations WHERE category IS NULL");
        for (String cat : categories) {
            qry.append(" OR category='").append(cat).append("'");
        }

        mysql.select(qry.toString(), rs -> {
            try {
                while (rs.next()) {
                    final Map<Language, String> values = new HashMap<>();
                    for (Language language : Language.values()) {
                        values.put(language, rs.getString(language.getId()));
                    }

                    translations.put(rs.getString("key").replaceAll("&", "§"), new TranslationField(values));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        */
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

    @Override
    @Deprecated
    public void insertIfNotExists(final Map<String, TranslationField> translations) {
        /*StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO translations (`key`");
        for (Language l : Language.values()) {
            sb.append(", `").append(l.getId()).append("`");
        }
        sb.append(") VALUES ");

        int i = 0;
        for (HashMap.Entry<String, TranslationField> entry : translations.entrySet()) {
            if (!this.translations.containsKey(entry.getKey())) {
                i++;
                this.translations.put(entry.getKey(), entry.getValue());
                sb.append("('").append(entry.getKey().toLowerCase()).append("'");

                for (String translation : entry.getValue().getTranslations()) {
                    if (translation == null) {
                        sb.append(", NULL");
                    } else {
                        sb.append(", '").append(translation).append("'");
                    }
                }
                sb.append(")");

                if (i == translations.size()) break;
                sb.append(", ");
            }
        }

        if (i > 0) mysql.update(sb.toString());*/
    }

}
