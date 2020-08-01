package eu.mcone.coresystem.api.bukkit.vanish;

import org.bukkit.entity.Player;

import java.util.List;

public interface VanishRule {

    void allowToSeePlayer(Player player, List<Player> canSeePlayer);

}
