package eu.mcone.coresystem.api.bukkit.inventory.modification;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenericDefaultInventory extends BasicInventory {

    private byte[] defaultItemsAsByteArray;

    public GenericDefaultInventory() {
    }

    public GenericDefaultInventory(final long lastUpdate, final String gamemode, final String category, final String name, final String title, final int size, final byte[] defaultItemsAsByteArray) {
        this.setLastUpdate(lastUpdate);
        this.setGamemode(gamemode);
        this.setCategory(category);
        this.setName(name);
        this.setTitle(title);
        this.setSize(size);
        this.defaultItemsAsByteArray = defaultItemsAsByteArray;
    }

    public DefaultInventory parseToDefaultInventory() {
        return new DefaultInventory(this.getLastUpdate(), this.getGamemode(), this.getCategory(), this.getName(), this.getTitle(), this.getSize(), InventoryModificationManager.fromByteArray(defaultItemsAsByteArray));
    }
}
