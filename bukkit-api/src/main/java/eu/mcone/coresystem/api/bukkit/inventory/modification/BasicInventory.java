package eu.mcone.coresystem.api.bukkit.inventory.modification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicInventory {
    private long lastUpdate;
    private String gamemode;
    private String category;
    private String name;
    private String title;
    private int size;

    public BasicInventory() {}
}
