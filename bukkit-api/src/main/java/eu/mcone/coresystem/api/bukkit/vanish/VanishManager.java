package eu.mcone.coresystem.api.bukkit.vanish;

import org.bukkit.entity.Player;

public interface VanishManager {

    void registerVanishRule(int priority, VanishRule rule);

    void recalculateVanishes();

    boolean shouldSee(Player player, Player shouldBeSeen);

    boolean showIfShouldBeSeen(Player target, Player shouldShow);

    boolean hideIfShouldBeHidden(Player target, Player shouldShow);
}
