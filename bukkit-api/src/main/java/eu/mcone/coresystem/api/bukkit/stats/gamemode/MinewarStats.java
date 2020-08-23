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
public class MinewarStats extends CoreStats {

    private int blocks;

    public MinewarStats(Gamemode gamemode, Player player) {
        this(gamemode, player.getUniqueId());
    }

    public MinewarStats(Gamemode gamemode, UUID uuid) {
        super(gamemode, uuid);
    }

    public void addBlocks(int blocks) {
        this.blocks += blocks;
    }

    public void removeBlocks(int blocks) {
        if ((this.blocks - blocks) >= 0) {
            this.blocks -= blocks;
        } else {
            this.blocks = 0;
        }
    }

    @Override
    public String toString() {
        return "MinewarStats{" +
                "blocks=" + blocks +
                '}';
    }
}
