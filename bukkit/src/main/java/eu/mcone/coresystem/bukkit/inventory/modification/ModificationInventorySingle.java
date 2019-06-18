package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifiedInventory;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory.EXIT_ITEM;

public class ModificationInventorySingle extends CoreInventory {

    public ModificationInventorySingle() {
        super(InventorySlot.ROW_5);
    }

    public void createInventory(Player player, String title, List<ModifyInventory> modifyInventories) {
        setTitle(title);

        //ROW 1
        for (int row1 = InventorySlot.ROW_1_SLOT_1; row1 <= InventorySlot.ROW_1_SLOT_9; row1++) {
            setItem(row1, EMPTY_SLOT_ITEM);
        }

        //ROW 3
        for (int row3 = InventorySlot.ROW_3_SLOT_1; row3 <= InventorySlot.ROW_3_SLOT_9; row3++) {
            setItem(row3, EMPTY_SLOT_ITEM);
        }

        int modified = InventorySlot.ROW_2_SLOT_1;
        int notModified = InventorySlot.ROW_4_SLOT_1;
        for (ModifyInventory modifyInventory : modifyInventories) {
            if (modifyInventory.getOptions().contains(Option.CAN_MODIFY)) {
                if (CoreSystem.getInstance().getInventoryModificationManager().isInventoryModified(player.getUniqueId(), modifyInventory.getInventoryKey())) {
                    ModifiedInventory modifiedInventory = CoreSystem.getInstance().getInventoryModificationManager().getModifiedInventory(player, modifyInventory);

                    setItem(modified, new ItemBuilder(Material.CHEST, 1).displayName("§a✔ " + modifiedInventory.getName())
                            .lore("§7§oDu hast dieses Inventar zuletzt am, ")
                            .lore("§f" + new SimpleDateFormat("dd.MM.yyy").format(new Date(modifiedInventory.getLastUpdate() * 1000)) + " §7§omodifiziert.")
                            .create(), e -> open(player, modifyInventory));
                    modified++;
                } else {
                    setItem(notModified, new ItemBuilder(Material.CHEST, 1).displayName("§c✖ " + modifyInventory.getTitle()).lore(
                            "§cDu hast dieses Inventar noch nicht modifiziert!"
                    ).create(), e -> open(player, modifyInventory));
                    notModified++;
                }
            }
        }

        setItem(InventorySlot.ROW_5_SLOT_9, EXIT_ITEM, e -> new ModificationInventory().createInventory(player, CoreSystem.getInstance().getInventoryModificationManager().getGamemode()));

        openInventory(player);
    }

    private Inventory open(Player player, ModifyInventory modifyInventory) {
        return CoreSystem.getInstance().getInventoryModificationManager().addCurrentlyModifying(player, modifyInventory).openInventory(player);
    }
}
