package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.translation.CoreTranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TranslationManager extends CoreTranslationManager {

    String get(String key, Player player);

    String get(String key, Player player, Object... replace);

    String get(String key, CommandSender sender);

    String get(String key, CommandSender sender, Object... replace);

}
