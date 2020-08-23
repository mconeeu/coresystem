package eu.mcone.coresystem.api.bukkit.stats;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CoreStats {

    private Gamemode gamemode;
    private UUID uuid;
    private int goals;
    private int losses;
    private int kills;
    private int deaths;

    public CoreStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public CoreStats(Gamemode gamemode, UUID uuid) {
        this.gamemode = gamemode;
        this.uuid = uuid;
    }

    public void addGoals(int goals) {
        this.goals += goals;
    }

    public void addLosses(int losses) {
        this.losses += losses;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    public void removeGoals(int goals) {
        if ((this.goals - goals) >= 0) {
            this.goals -= goals;
        } else {
            this.goals = 0;
        }
    }

    public void removeLoss(int losses) {
        if ((this.losses - losses) >= 0) {
            this.losses -= losses;
        } else {
            this.losses = 0;
        }
    }

    public void removeKills(int kills) {
        if ((this.kills - kills) >= 0) {
            this.kills -= kills;
        } else {
            this.kills = 0;
        }
    }

    public void removeDeaths(int deaths) {
        if ((this.deaths - deaths) >= 0) {
            this.deaths -= deaths;
        } else {
            this.deaths = 0;
        }
    }

    @BsonIgnore
    public double getKD() {
        double kill = kills;
        double death = deaths;

        if (kill == 0 && death == 0) {
            return 0.00D;
        } else if (kill == 0) {
            return 0.00D;
        } else if (death == 0) {
            return kill + .00D;
        } else {
            return kill / death;
        }
    }

    @BsonIgnore
    public int getRounds() {
        return losses + goals;
    }

    @BsonIgnore
    public int getWinPercent() {
        return (getRounds() / 100) * goals;
    }

    @BsonIgnore
    public int getLostPercent() {
        return (getRounds() / 100) * losses;
    }

    @Override
    public String toString() {
        return "CoreStats{" +
                "gamemode=" + gamemode +
                ", uuid=" + uuid +
                ", goals=" + goals +
                ", losses=" + losses +
                ", kills=" + kills +
                ", deaths=" + deaths +
                '}';
    }
}
