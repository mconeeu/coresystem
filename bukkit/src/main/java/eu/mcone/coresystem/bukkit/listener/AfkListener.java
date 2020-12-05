package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.player.AfkEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.player.CoreAfkManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

@RequiredArgsConstructor
public class AfkListener implements Listener {

    private final CoreAfkManager manager;

    @EventHandler
    public void on(PlayerToggleSneakEvent e) {
        manager.setAfk(e.getPlayer(), false);
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent e) {
        manager.setAfk(e.getPlayer(), false);
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        manager.setAfk(e.getPlayer(), false);
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        manager.setAfk(e.getPlayer(), false);
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent e) {
        manager.setAfk(e.getPlayer(), false);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            manager.setAfk((Player) e.getDamager(), false);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        manager.setAfk(e.getPlayer(), false);
    }

    @EventHandler
    public void on(AfkEvent e) {
        for (CorePlayer cp : CoreSystem.getInstance().getOnlineCorePlayers()) {
            if (cp.hasPermission("group.team") || cp.bukkit().equals(e.getPlayer())) {
                cp.getScoreboard().reload();
            }
        }
    }

}
