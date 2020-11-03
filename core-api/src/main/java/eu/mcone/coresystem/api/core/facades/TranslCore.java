package eu.mcone.coresystem.api.core.facades;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.CoreTranslationManager;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import lombok.Setter;

public class TranslCore {

    @Setter
    private static CoreTranslationManager manager;

    public static TranslationField getTranslations(String key) {
        return manager.getTranslations(key);
    }

    public static String get(String key) {
        return manager.get(key);
    }

    public static String get(String key, Language language) {
        return manager.get(key, language);
    }

    public static String get(String key, Language language, Object... replace) {
        return manager.get(key, language, replace);
    }

    public static String get(String key, GlobalCorePlayer player) {
        return manager.get(key, player);
    }

    public static String get(String key, GlobalCorePlayer player, Object... replace) {
        return manager.get(key, player, replace);
    }

}
