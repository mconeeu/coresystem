package eu.mcone.coresystem.api.bukkit.stats.gamemode;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.stats.CoreStats;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BedwarsStats extends CoreStats {

    private int beds;
    private int kits;
    private int specials;

    public BedwarsStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public BedwarsStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    public void addBeds(int beds) {
        this.beds += beds;
    }

    public void addKits(int kits) {
        this.kits += kits;
    }

    public void addSpecials(int specials) {
        this.specials += specials;
    }

    public void removeBeds(int beds) {
        if ((this.beds - beds) >= 0) {
            this.beds -= beds;
        } else {
            this.beds = 0;
        }
    }

    public void removeKits(int kits) {
        if ((this.kits - kits) >= 0) {
            this.kits -= kits;
        } else {
            this.kits = 0;
        }
    }

    public void removeSpecials(int specials) {
        if ((this.specials - specials) >= 0) {
            this.specials -= specials;
        } else {
            this.specials = 0;
        }
    }

    @Override
    public String toString() {
        return "BedwarsStats{" +
                "beds=" + beds +
                ", kits=" + kits +
                ", specials=" + specials +
                '}';
    }
}
