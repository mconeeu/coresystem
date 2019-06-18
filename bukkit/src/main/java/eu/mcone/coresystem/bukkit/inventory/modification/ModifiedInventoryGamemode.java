package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory.EXIT_ITEM;

public class ModifiedInventoryGamemode extends CoreInventory {

    public ModifiedInventoryGamemode() {
        super(InventorySlot.ROW_5, Option.FILL_EMPTY_SLOTS);
    }

    public void createInventory(final Player player, final Gamemode gamemode) {
        setTitle("§8» " + gamemode.getColor() + gamemode.getName());

        List<ModifyInventory> inventories = CoreSystem.getInstance().getInventoryModificationManager().getInventories(gamemode);
        if (inventories.isEmpty()) {
            setItem(InventorySlot.ROW_3_SLOT_5, new ItemBuilder(Material.BARRIER, 1).displayName("§cNicht verfügbar!").lore(
                    "§7§oFür diesen Gamemode sind kein,",
                    "§7§oInventare zum modifizieren verfügbar!"
            ).create());
        } else {
            ItemBuilder singleInventoriesItem = new ItemBuilder(Material.BOOK).displayName("§f§oOhne Kategorie").lore("§7§oHier findest du alle Inventare, ", "§7§odie §ckeiner §7§oKategorie angehören");
            CoreItemEvent coreItemEvent = e -> new ModificationInventorySingle().createInventory(player, "§8» §c§lModifizieren", CoreSystem.getInstance().getInventoryModificationManager().getInventories(gamemode));

            if (!CoreSystem.getInstance().getInventoryModificationManager().getMultipleInventories().isEmpty()) {
                setItem(InventorySlot.ROW_3_SLOT_3, singleInventoriesItem.create(), coreItemEvent);
                setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.BOOKSHELF).displayName("§f§oMit Kategorie").lore("§7§oHier findest du alle Inventare, ", "§7§odie §aeiner §7§oKategorie angehören").create(), e -> new ModificationInventoryMultiple().createInventory(player, CoreSystem.getInstance().getInventoryModificationManager().getMultipleInventoriesWhereGamemode(gamemode)));
            } else {
                setItem(InventorySlot.ROW_3_SLOT_5, singleInventoriesItem.create(), coreItemEvent);
            }
        }

        setItem(InventorySlot.ROW_5_SLOT_9, EXIT_ITEM, e -> new ModificationInventory().createInventory(player, CoreSystem.getInstance().getInventoryModificationManager().getGamemode()));

        super.openInventory(player);
    }
}
