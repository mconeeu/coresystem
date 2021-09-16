package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.translation.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

    @Override
    public void registerTranslationKeys(FileConfiguration config, String pluginSlug) {
        ArrayList<String> list = (ArrayList<String>) config.getList("translations");

        if (list != null && !list.isEmpty()) {
            CoreSystem.getInstance().getTranslationManager().registerKeys(pluginSlug, list);
        }
    }

}
