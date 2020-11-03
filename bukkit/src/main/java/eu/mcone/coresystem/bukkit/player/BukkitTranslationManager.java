package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.translation.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitTranslationManager extends TranslationManager implements eu.mcone.coresystem.api.bukkit.player.TranslationManager {

    public BukkitTranslationManager(CoreModuleCoreSystem system, String... categories) {
        super(system, categories);
    }

    @Override
    public String get(String key, Player player) {
        return get(key, CoreSystem.getInstance().getCorePlayer(player).getSettings().getLanguage());
    }

    @Override
    public String get(String key, Player player, Object... replace) {
        return get(key, CoreSystem.getInstance().getCorePlayer(player).getSettings().getLanguage(), replace);
    }

    @Override
    public String get(String key, CommandSender sender) {
        if (sender instanceof Player) {
            return get(key, (Player) sender);
        }

        return get(key, DEFAULT_LANGUAGE);
    }

    @Override
    public String get(String key, CommandSender sender, Object... replace) {
        if (sender instanceof Player) {
            return get(key, (Player) sender, replace);
        }

        return get(key, DEFAULT_LANGUAGE, replace);
    }

}
