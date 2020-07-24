package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import io.sentry.event.BreadcrumbBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SentryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        recordBreadcrumb(e.getPlayer(), "Join", "joined", new HashMap<String, String>(){{
            put("quitMesage", e.getJoinMessage());
        }});
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent e) {
        recordBreadcrumb(e.getPlayer(), "Quit", "leaved", new HashMap<String, String>(){{
            put("quitMesage", e.getQuitMessage());
        }});
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        recordBreadcrumb(e.getPlayer(), "Command", "performed Command "+e.getMessage(), new HashMap<String, String>(){{
            put("command", e.getMessage());
            put("cancelled", String.valueOf(e.isCancelled()));
        }});
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (CoreSystem.getInstance().getPluginManager().getCurrentCoreInventory((Player) e.getPlayer()).getInventory().equals(e.getInventory())) {
            Player p = (Player) e.getPlayer();
            Inventory inv = e.getInventory();

            recordBreadcrumb(p, "CoreInventory", "opens CoreInventory "+inv.getName(), new HashMap<String, String>(){{
                put("title", inv.getTitle());
                put("type", String.valueOf(inv.getType()));
                put("size", String.valueOf(inv.getSize()));
                put("cancelled", String.valueOf(e.isCancelled()));
            }});
        }
    }

    private static void recordBreadcrumb(Player p, String category, String message) {
        recordBreadcrumb(p, category, message, new HashMap<>());
    }

    private static void recordBreadcrumb(Player p, String category, String message, Map<String, String> data) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof CorePlugin) {
                CorePlugin corePlugin = (CorePlugin) plugin;

                if (corePlugin.hasSentryClient()) {
                    data.put("name", p.getName());
                    data.put("uuid", p.getUniqueId().toString());

                    corePlugin.getSentryClient().getContext().recordBreadcrumb(
                            new BreadcrumbBuilder().setCategory(category).setMessage("Player "+message).setData(data).build()
                    );
                }
            }
        }
    }

}
