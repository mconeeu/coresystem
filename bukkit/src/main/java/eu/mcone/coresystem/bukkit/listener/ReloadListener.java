package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        Player p = e.getPlayer();

        if (p.hasPermission("system.bukkit.reload") && cmd.equalsIgnoreCase("/rl") || cmd.equalsIgnoreCase("/reload")) {
            e.setCancelled(true);
            BukkitCoreSystem.getSystem().getMessenger().send(p, "§7Der Server wird gereloadet...");

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("group.team") && !player.equals(p)) {
                    BukkitCoreSystem.getSystem().getMessenger().send(player, "§f§o"+p.getName()+"§7§o reloadet den Server...");
                }
            }

            Bukkit.reload();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("group.team") || player.equals(p)) {
                    BukkitCoreSystem.getSystem().getMessenger().send(player, "§2§oDer Server wurde erfolgreich gereloadet!");
                }
            }
        }
    }

}
