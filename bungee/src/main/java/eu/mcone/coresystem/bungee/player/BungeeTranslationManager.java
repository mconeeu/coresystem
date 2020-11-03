package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.translation.TranslationManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeTranslationManager extends TranslationManager implements eu.mcone.coresystem.api.bungee.player.TranslationManager {

    public BungeeTranslationManager(CoreModuleCoreSystem system, String... categories) {
        super(system, categories);
    }

    @Override
    public String get(String key, ProxiedPlayer player) {
        return get(key, CoreSystem.getInstance().getCorePlayer(player).getSettings().getLanguage());
    }

    @Override
    public String get(String key, ProxiedPlayer player, Object... replace) {
        return get(key, CoreSystem.getInstance().getCorePlayer(player).getSettings().getLanguage(), replace);
    }

    @Override
    public String get(String key, CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return get(key, (ProxiedPlayer) sender);
        }

        return get(key, DEFAULT_LANGUAGE);
    }

    @Override
    public String get(String key, CommandSender sender, Object... replace) {
        if (sender instanceof ProxiedPlayer) {
            return get(key, (ProxiedPlayer) sender, replace);
        }

        return get(key, DEFAULT_LANGUAGE, replace);
    }

}
