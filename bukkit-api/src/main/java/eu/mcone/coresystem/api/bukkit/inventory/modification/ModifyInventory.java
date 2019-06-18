package eu.mcone.coresystem.api.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ModifyInventory extends CoreInventory {

    public static ItemStack EXIT_ITEM = new ItemBuilder(Material.IRON_DOOR).displayName("§c§oZurück").create();

    @Setter
    @Getter
    private Gamemode gamemode = Gamemode.UNDEFINED;
    @Getter
    private String category;
    @Getter
    private String name;
    @Getter
    private HashMap<Integer, UniqueItemStack> uniqueItemStacks;

    /**
     * creates new CoreInventory
     *
     * @param name inventory title
     * @param size inventory size
     * @param args options
     */
    public ModifyInventory(String name, String title, int size, Option... args) {
        super(title, size, args);
        this.name = name;
        this.uniqueItemStacks = new HashMap<>();
    }

    /**
     * creates new CoreInventory
     *
     * @param name     inventory title
     * @param category category of the inventory
     * @param size     inventory size
     * @param args     options
     */
    public ModifyInventory(String name, String title, String category, int size, Option... args) {
        super(title, size, args);
        this.name = name;
        this.category = category;
        this.uniqueItemStacks = new HashMap<>();
    }


    @Override
    public void setItem(int slot, ItemStack item, CoreItemEvent event) {
        UniqueItemStack uniqueItemStack = new UniqueItemStack(UUID.randomUUID(), item);
        uniqueItemStacks.put(slot, uniqueItemStack);
        super.setItem(slot, item, event);
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        UniqueItemStack uniqueItemStack = new UniqueItemStack(UUID.randomUUID(), item);
        uniqueItemStacks.put(slot, uniqueItemStack);
        super.setItem(slot, item);
    }

    public Inventory openInventory(final Player player) {
        //Check if Inventory is modified
        if (CoreSystem.getInstance().getInventoryModificationManager().isInventoryModified(player.getUniqueId(), getInventoryKey())) {
            //Create inventory from modified and default inventory data
            DefaultInventory defaultInventory = CoreSystem.getInstance().getInventoryModificationManager().getDefaultInventory(getInventoryKey());
            ModifiedInventory modifiedInventory = CoreSystem.getInstance().getInventoryModificationManager().getModifiedInventory(player, this);
            if (defaultInventory != null && modifiedInventory != null) {
                Inventory inventory = Bukkit.createInventory(null, getSize(), getTitle());
                for (Map.Entry<String, UUID> modifiedEntry : modifiedInventory.getUniqueItemStack().entrySet()) {
                    for (Map.Entry<Integer, ModifyInventory.UniqueItemStack> defaultEntry : defaultInventory.getDefaultItemsAsMap().entrySet()) {
                        if (defaultEntry.getValue().getUuid().equals(modifiedEntry.getValue())) {
                            inventory.setItem(Integer.valueOf(modifiedEntry.getKey()), defaultEntry.getValue().getItemStack());
                            break;
                        }
                    }
                }

                player.openInventory(inventory);
                return inventory;
            } else {
                CoreSystem.getInstance().getMessager().send(player, "§cDas Inventar konnte nicht geöffnet werden, §f§omelde dies bitte einem MCONE Teammitglied!");
                player.closeInventory();
                return null;
            }
        } else {
            Inventory inventory = Bukkit.createInventory(null, getSize(), getTitle());

            if (getOptions().contains(Option.FILL_EMPTY_SLOTS)) {
                for (int i = 0; i < getSize(); i++) {
                    inventory.setItem(i, EMPTY_SLOT_ITEM);
                }
            }

            for (Map.Entry<Integer, CoreItemStack> entry : getItems().entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
            }

            player.openInventory(inventory);
            return inventory;
        }
    }

    public String getInventoryKey() {
        return gamemode + "." + category + "." + name;
    }

    @Getter
    @AllArgsConstructor
    public static class UniqueItemStack implements Serializable {
        private final UUID uuid;
        private final ItemStack itemStack;
    }
}
