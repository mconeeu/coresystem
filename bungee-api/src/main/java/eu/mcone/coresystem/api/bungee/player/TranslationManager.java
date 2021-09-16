package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.translation.CoreTranslationManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public interface TranslationManager extends CoreTranslationManager {
    String get(String key, ProxiedPlayer player);

    String get(String key, ProxiedPlayer player, Object... replace);

    String get(String key, CommandSender sender);

    String get(String key, CommandSender sender, Object... replace);

    void registerTranslationKeys(Configuration config, String pluginSlug);
}
