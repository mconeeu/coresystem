package eu.mcone.coresystem.api.bukkit.scoreboard;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.util.Random;
import lombok.Getter;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
public class CoreScoreboardEntry {

    private static final Random TEAM_RANDOM = new Random(6);

    private final CorePlayer player;
    private int priority;
    private String name, prefix, suffix;

    CoreScoreboardEntry(CorePlayer player) {
        this.player = player;
    }

    public CoreScoreboardEntry priority(int priority) {
        this.priority = priority;
        return this;
    }

    public CoreScoreboardEntry name(String name) {
        this.name = name;
        return this;
    }

    public CoreScoreboardEntry prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public CoreScoreboardEntry suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    Team setTeam(Scoreboard scoreboard) {
        Team team = scoreboard.registerNewTeam(priority + "_" + TEAM_RANDOM.nextString());
        if (name != null) {
            team.setDisplayName(name);
        }
        if (prefix != null) {
            team.setPrefix(prefix);
        }
        if (suffix != null) {
            team.setSuffix(suffix);
        }
        team.addEntry(player.isNicked() ? player.getNick().getName() : player.getName());

        return team;
    }

}
