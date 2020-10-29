package eu.mcone.coresystem.api.bukkit.listener;

import org.bukkit.Achievement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class PlayerAchievementAwardedCanceller implements Listener {

    private final Achievement[] achievements;

    public PlayerAchievementAwardedCanceller(Achievement... achievements) {
        this.achievements = achievements;
    }

    @EventHandler
    public void on(PlayerAchievementAwardedEvent e) {
        if (achievements.length > 0) {
            for (Achievement achievement : achievements) {
                if (e.getAchievement().equals(achievement)) {
                    e.setCancelled(true);
                    return;
                }
            }
        } else {
            e.setCancelled(true);
        }
    }

}
