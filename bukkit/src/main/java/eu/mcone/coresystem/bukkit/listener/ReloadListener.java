package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class ReloadListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        Player p = e.getPlayer();

        if (cmd.equalsIgnoreCase("/rl") || cmd.equalsIgnoreCase("/reload")) {
            if (p.hasPermission("system.bukkit.reload")) {
                CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

                e.setCancelled(true);
                BukkitCoreSystem.getSystem().getMessenger().send(p, "§7Der Server wird gereloadet...");

                List<Player> notifiable = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("group.team") && !player.equals(p)) {
                        notifiable.add(player);
                    }
                }

                for (Player player : notifiable) {
                    BukkitCoreSystem.getSystem().getMessenger().send(player, "§f§o" + cp.getName() + "§7§o reloadet den Server...");
                }

                Bukkit.reload();

                notifiable.add(p);
                for (Player player : notifiable) {
                    BukkitCoreSystem.getSystem().getMessenger().send(player, "§2§oDer Server wurde erfolgreich gereloadet!");
                }
            } else {
                e.setCancelled(true);
                CoreSystem.getInstance().getMessenger().sendTransl(p, "system.command.noperm");
            }
        }
    }

}
