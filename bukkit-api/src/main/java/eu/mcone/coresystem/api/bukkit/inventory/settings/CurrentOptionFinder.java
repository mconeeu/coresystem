package eu.mcone.coresystem.api.bukkit.inventory.settings;

import org.bukkit.entity.Player;

public interface CurrentOptionFinder<T> {

    T getCurrentOption(Player player);

}
