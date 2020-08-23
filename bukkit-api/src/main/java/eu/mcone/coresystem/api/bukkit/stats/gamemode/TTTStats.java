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
public class TTTStats extends CoreStats {

    private int traitors;
    private int detectives;

    public TTTStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public TTTStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    public void addTraitors(int traitors) {
        this.traitors += traitors;
    }

    public void addDetectives(int detectives) {
        this.detectives += detectives;
    }

    public void removeTraitors(int traitors) {
        if ((this.traitors - traitors) >= 0) {
            this.traitors -= traitors;
        } else {
            this.traitors = 0;
        }
    }

    public void removeDetectives(int detectives) {
        if ((this.detectives - detectives) >= 0) {
            this.detectives -= detectives;
        } else {
            this.detectives = 0;
        }
    }

    @Override
    public String toString() {
        return "TTTStats{" +
                "traitors=" + traitors +
                ", detectives=" + detectives +
                '}';
    }
}
