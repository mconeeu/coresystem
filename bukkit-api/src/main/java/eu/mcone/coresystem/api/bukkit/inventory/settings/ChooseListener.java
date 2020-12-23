package eu.mcone.coresystem.api.bukkit.inventory.settings;

import org.bukkit.entity.Player;

public interface ChooseListener<T> {

    void onChosen(Player p, T chosen);

}
