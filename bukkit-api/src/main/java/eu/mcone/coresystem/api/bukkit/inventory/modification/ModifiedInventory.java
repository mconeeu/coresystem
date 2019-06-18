package eu.mcone.coresystem.api.bukkit.inventory.modification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class ModifiedInventory extends BasicInventory{

    private Map<String, UUID> uniqueItemStack;

    public ModifiedInventory(final String gamemode, final String category, final String name, final String title, final int size, final Map<String, UUID> uniqueItemStack) {
        this.setLastUpdate(System.currentTimeMillis() / 1000);
        this.setGamemode(gamemode);
        this.setCategory(category);
        this.setName(name);
        this.setTitle(title);
        this.setSize(size);
        this.uniqueItemStack = uniqueItemStack;
    }

    public ModifiedInventory(final Document document) {
        this.setLastUpdate(document.getLong("lastUpdate"));
        this.setGamemode(document.getString("gamemode"));
        this.setCategory(document.getString("category"));
        this.setName(document.getString("name"));
        this.setTitle(document.getString("title"));
        this.setSize(document.getInteger("size"));
        this.uniqueItemStack = (Map<String, UUID>) document.get("uniqueItemStack");
    }
}
