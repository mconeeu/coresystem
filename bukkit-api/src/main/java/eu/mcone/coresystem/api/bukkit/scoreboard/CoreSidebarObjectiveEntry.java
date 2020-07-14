package eu.mcone.coresystem.api.bukkit.scoreboard;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class CoreSidebarObjectiveEntry {

    @Getter
    @Setter
    private Map<Integer, String> scores;
    @Getter
    @Setter
    private String title;

    public CoreSidebarObjectiveEntry() {
        scores = new HashMap<>();
    }

    public void setScore(int score, String value) {
        scores.put(score, value);
    }

}
