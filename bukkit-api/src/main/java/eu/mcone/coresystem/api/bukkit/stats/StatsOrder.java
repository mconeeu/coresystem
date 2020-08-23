package eu.mcone.coresystem.api.bukkit.stats;

import lombok.Getter;

@Getter
public enum StatsOrder {

    KILLS("kills"),
    DEATHS("deaths"),
    WINS("wins"),
    LOSSES("losses"),
    KD(""),
    ROUNDS("rounds");

    private final String mongoField;

    StatsOrder(String mongoField) {
        this.mongoField = mongoField;
    }
}
