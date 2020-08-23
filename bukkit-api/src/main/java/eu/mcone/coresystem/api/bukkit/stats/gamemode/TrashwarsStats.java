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
public class TrashwarsStats extends CoreStats {

    private int chests;

    public TrashwarsStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public TrashwarsStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    public void addChests(int chests) {
        this.chests += chests;
    }

    public void removeChests(int chests) {
        if ((this.chests - chests) >= 0) {
            this.chests -= chests;
        } else {
            this.chests = 0;
        }
    }

    @Override
    public String toString() {
        return "TrashwarsStats{" +
                "chests=" + chests +
                '}';
    }
}
