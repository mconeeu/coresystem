package eu.mcone.coresystem.api.bukkit.vanish;

import org.bukkit.entity.Player;

import java.util.List;

public interface VanishRule {

    void visibleForPlayer(Player player, List<Player> playerCanSee);

}
