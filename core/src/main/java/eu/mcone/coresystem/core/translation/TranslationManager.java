/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.translation;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import eu.mcone.coresystem.core.mysql.MySQL;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TranslationManager implements eu.mcone.coresystem.api.core.translation.TranslationManager {

    private MySQL mysql;
    private Map<String, TranslationField> translations;

    public TranslationManager(MySQL mysql) {
        this.mysql = mysql;
        this.translations = new HashMap<>();

        reload();
    }

    @Override
    public void reload() {
        translations.clear();

        mysql.select("SELECT * FROM bungeesystem_translations", rs -> {
            try {
                while (rs.next()) {
                    final Map<Language, String> values = new HashMap<>();
                    for (Language language : Language.values()) {
                        values.put(language, rs.getString(language.getId()));
                    }

                    translations.put(rs.getString("key"), new TranslationField(values));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public TranslationField getTranslations(String key) {
        return translations.get(key);
    }

    @Override
    public String get(String key) {
        return translations.get(key).getString(Language.GERMAN);
    }

    @Override
    public String get(String key, Language language) {
        return getTranslation(key, language);
    }

    @Override
    public String get(String key, GlobalCorePlayer player) {
        return get(key, player.getLanguage());
    }

    private String getTranslation(String key, Language language) {
        String result = translations.get(key).getString(language);

        if (result != null) {
            return result;
        } else {
            result = translations.get(key).getString(Language.GERMAN);
            return result;
        }
    }

    @Override
    public void insertIfNotExists(Map<String, TranslationField> translations) {
        Map<String, TranslationField> insert = new HashMap<>();

        for (HashMap.Entry<String, TranslationField> entry : translations.entrySet()) {
            if (!this.translations.containsKey(entry.getKey())) {
                insert.put(entry.getKey(), entry.getValue());
            }
        }

        if (insert.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO bungeesystem_translations (`key`");
            for (Language l : Language.values()) {
                sb.append(", `").append(l.getId()).append("`");
            }
            sb.append(") VALUES ");

            int i = 0;
            for (HashMap.Entry<String, TranslationField> entry : insert.entrySet()) {
                i++;
                translations.put(entry.getKey(), entry.getValue());
                sb.append("('").append(entry.getKey().toLowerCase()).append("'");

                for (String translation : entry.getValue().getTranslations()) {
                    if (translation == null) {
                        sb.append(", NULL");
                    } else {
                        sb.append(", '").append(translation).append("'");
                    }
                }
                sb.append(")");

                if (i == insert.size()) return;
                sb.append(", ");
            }

            mysql.update(sb.toString());
        }
    }

}
