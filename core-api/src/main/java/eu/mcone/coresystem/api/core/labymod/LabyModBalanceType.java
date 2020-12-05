package eu.mcone.coresystem.api.core.labymod;

import lombok.Getter;

@Getter
public enum LabyModBalanceType {

    CASH("cash"),
    BANK("bank");

    private final String key;

    LabyModBalanceType(String key) {
        this.key = key;
    }

}
