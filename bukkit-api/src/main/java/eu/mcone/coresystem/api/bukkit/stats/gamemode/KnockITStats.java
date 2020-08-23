package eu.mcone.coresystem.api.bukkit.stats.gamemode;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.stats.CoreStats;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@BsonDiscriminator
public class KnockITStats extends CoreStats {

    private int killStreak;

    public KnockITStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public KnockITStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    public void addStreaks(int streaks) {
        this.killStreak += streaks;
    }

    public void removeStreaks(int streaks) {
        if ((this.killStreak - streaks) >= 0) {
            this.killStreak -= streaks;
        } else {
            this.killStreak = 0;
        }
    }

    @Override
    public String toString() {
        return "KnockITStats{" +
                "killStreak=" + killStreak +
                '}';
    }
}
