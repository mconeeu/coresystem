package eu.mcone.coresystem.api.bukkit.inventory.modification;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DefaultInventory extends GenericDefaultInventory {

    private Map<Integer, ModifyInventory.UniqueItemStack> defaultItemsAsMap;

    public DefaultInventory() {}

    public DefaultInventory(final long lastUpdate, final String gamemode, final String category, final String name, final String title, final int size, final Map<Integer, ModifyInventory.UniqueItemStack> defaultItems) {
        super(lastUpdate, gamemode, category, name, title, size, null);
        this.defaultItemsAsMap = defaultItems;
    }

    public GenericDefaultInventory parseToGenericInventory() {
        return new GenericDefaultInventory(getLastUpdate(), getGamemode(), getCategory(), getName(), getTitle(), getSize(), InventoryModificationManager.toByteArray(defaultItemsAsMap));
    }
}
