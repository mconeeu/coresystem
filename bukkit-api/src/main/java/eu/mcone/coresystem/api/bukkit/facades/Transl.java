package eu.mcone.coresystem.api.bukkit.facades;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.TranslationManager;
import eu.mcone.coresystem.api.core.facades.TranslCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Transl extends TranslCore {

    private static final TranslationManager MANAGER = CoreSystem.getInstance().getTranslationManager();

    public static String get(String key, Player player) {
        return MANAGER.get(key, player);
    }

    public static String get(String key, Player player, Object... replace) {
        return MANAGER.get(key, player, replace);
    }

    public static String get(String key, CommandSender sender) {
        return MANAGER.get(key, sender);
    }

    public static String get(String key, CommandSender sender, Object... replace) {
        return MANAGER.get(key, sender, replace);
    }

}
