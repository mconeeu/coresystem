package eu.mcone.coresystem.api.bungee.facades;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.TranslationManager;
import eu.mcone.coresystem.api.core.facades.TranslCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Transl extends TranslCore {

    private static final TranslationManager MANAGER = CoreSystem.getInstance().getTranslationManager();

    public static String get(String key, ProxiedPlayer player) {
        return MANAGER.get(key, player);
    }

    public static String get(String key, ProxiedPlayer player, Object... replace) {
        return MANAGER.get(key, player, replace);
    }

    public static String get(String key, CommandSender sender) {
        return MANAGER.get(key, sender);
    }

    public static String get(String key, CommandSender sender, Object... replace) {
        return MANAGER.get(key, sender, replace);
    }

}
