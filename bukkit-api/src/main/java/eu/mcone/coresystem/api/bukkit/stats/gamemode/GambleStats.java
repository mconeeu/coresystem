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
public class GambleStats extends CoreStats {

    public GambleStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public GambleStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
