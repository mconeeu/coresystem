package eu.mcone.coresystem.api.bukkit.inventory.modify;

import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CoreInventoryInitializeEntry {

    private String title;
    private int size;
    private Set<InventoryOption> options;

    public CoreInventoryInitializeEntry title(String title) {
        this.title = title;
        return this;
    }

    public CoreInventoryInitializeEntry size(int size) {
        this.size = size;
        return this;
    }

    public CoreInventoryInitializeEntry options(InventoryOption... options) {
        this.options = new HashSet<>(Arrays.asList(options));
        return this;
    }

    public CoreInventoryInitializeEntry addOption(InventoryOption option) {
        this.options.add(option);
        return this;
    }

    public CoreInventoryInitializeEntry removeOption(InventoryOption option) {
        this.options.remove(option);
        return this;
    }

}
