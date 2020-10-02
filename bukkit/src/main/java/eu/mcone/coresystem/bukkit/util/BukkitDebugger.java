package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.debugger.DebuggerInventory;
import eu.mcone.coresystem.core.util.CoreDebugger;
import org.bukkit.entity.Player;

public class BukkitDebugger extends CoreDebugger<Player> implements eu.mcone.coresystem.api.bukkit.util.BukkitDebugger {

    public BukkitDebugger(BukkitCoreSystem bukkitCoreSystem) {
        super(bukkitCoreSystem);
    }

    public void openDebuggerInventory(Player player) {
        new DebuggerInventory(this, player);
    }
}
