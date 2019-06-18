package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

import static eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory.EXIT_ITEM;

public class ModificationInventoryMultiple extends CoreInventory {

    public ModificationInventoryMultiple() {
        super(InventorySlot.ROW_5);
    }

    public void createInventory(Player player, Map<String, Map<String, ModifyInventory>> multipleInventories) {
        setTitle("§8» §c§lModifizieren");

        //ROW 1
        for (int row1 = InventorySlot.ROW_1_SLOT_1; row1 < InventorySlot.ROW_1_SLOT_9; row1++) {
            setItem(row1, EMPTY_SLOT_ITEM);
        }

        int i = InventorySlot.ROW_2_SLOT_1;
        for (Map.Entry<String, Map<String, ModifyInventory>> multipleInventoryEntry : multipleInventories.entrySet()) {
            String category = multipleInventoryEntry.getKey();

            ItemBuilder categoryItem = new ItemBuilder(Material.ENDER_CHEST, 1).displayName("§8» §f§o" + category);
            categoryItem.lore("§7§oDiese Kategorie, ", "§7§obeinhaltet folgenede Inventare,");

            for (Map.Entry<String, ModifyInventory> singleInventoryEntry : multipleInventoryEntry.getValue().entrySet()) {
                if (singleInventoryEntry.getValue().getOptions().contains(Option.CAN_MODIFY)) {
                    String inventoryTitle = singleInventoryEntry.getValue().getTitle();

                    if (CoreSystem.getInstance().getInventoryModificationManager().isInventoryModified(player.getUniqueId(), singleInventoryEntry.getValue().getInventoryKey())) {
                        categoryItem.addLore("  §8➥ §a§o" + inventoryTitle);
                    } else {
                        categoryItem.addLore("  §8➥ §c§o" + inventoryTitle);
                    }
                }
            }

            setItem(i, categoryItem.create(), e -> new ModificationInventorySingle().createInventory(player, category, new ArrayList<>(multipleInventoryEntry.getValue().values())));
            i++;

            setItem(InventorySlot.ROW_5_SLOT_9, EXIT_ITEM, e -> new ModificationInventory().createInventory(player, CoreSystem.getInstance().getInventoryModificationManager().getGamemode()));

            openInventory(player);
        }
    }
}
