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
public class OneAttackStats extends CoreStats {

    private int defended;
    private int attacked;

    public OneAttackStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public OneAttackStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    public void addDefends(int defends) {
        this.defended += defends;
    }

    public void addAttacks(int attacks) {
        this.attacked += attacks;
    }

    public void removeDefends(int defends) {
        if ((this.defended - defends) >= 0) {
            this.defended -= defends;
        } else {
            this.defended = 0;
        }
    }

    public void removeAttacks(int attacks) {
        if ((this.attacked - attacks) >= 0) {
            this.attacked -= attacks;
        } else {
            this.attacked = 0;
        }
    }

    @Override
    public String toString() {
        return "OneAttackStats{" +
                "defended=" + defended +
                ", attacked=" + attacked +
                '}';
    }
}
