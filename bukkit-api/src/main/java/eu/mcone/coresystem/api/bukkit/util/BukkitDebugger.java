package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.core.util.CoreDebugger;
import org.bukkit.entity.Player;

public interface BukkitDebugger extends CoreDebugger<Player> {

    void openDebuggerInventory(org.bukkit.entity.Player player);

}
