package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ModificationInventory extends CoreInventory {

    public ModificationInventory() {
        super("§8» §c§lInventories", InventorySlot.ROW_5, Option.FILL_EMPTY_SLOTS);
    }

    public void createInventory(Player player, final Gamemode localGamemode) {
        if (CoreSystem.getInstance().getInventoryModificationManager().getInventories().isEmpty()) {
            setItem(InventorySlot.ROW_3_SLOT_5, new ItemBuilder(Material.INK_SACK, 1, 1)
                    .displayName("§cNicht verfügbar!")
                    .lore("§cEs sind/ist kein(e) Inventar(e),")
                    .lore("zum editieren verfügbar!")
                    .create());
        } else {
            int i = InventorySlot.ROW_3_SLOT_3;
            //For all Inventories
            if (localGamemode.equals(Gamemode.UNDEFINED)) {
                for (Gamemode gamemode : Gamemode.values()) {
                    ItemStack itemStack = new ItemBuilder(gamemode.getItem()).displayName(gamemode.getColor() + gamemode.getName()).create();
                    CoreItemEvent event = e -> new ModifiedInventoryGamemode().createInventory(player, gamemode);

                    if (gamemode.equals(Gamemode.UNDEFINED)) {
                        setItem(InventorySlot.ROW_4_SLOT_5, itemStack, event);
                    } else {
                        setItem(i, itemStack, event);
                        i++;
                    }
                }
            } else {
                ItemBuilder singleInventoriesItem = new ItemBuilder(Material.BOOK).displayName("§f§oOhne Kategorie").lore("§7§oHier findest du alle Inventare, ", "§7§odie §ckeiner §7§oKategorie angehören");
                CoreItemEvent coreItemEvent = e -> new ModificationInventorySingle().createInventory(player, "§8» §c§lModifizieren", CoreSystem.getInstance().getInventoryModificationManager().getInventoriesWithOutCategory());

                if (!CoreSystem.getInstance().getInventoryModificationManager().getMultipleInventories().isEmpty()) {
                    setItem(InventorySlot.ROW_3_SLOT_3, singleInventoriesItem.create(), coreItemEvent);
                    setItem(InventorySlot.ROW_3_SLOT_7, new ItemBuilder(Material.BOOKSHELF).displayName("§f§oMit Kategorie").lore("§7§oHier findest du alle Inventare, ", "§7§odie §aeiner §7§oKategorie angehören").create(), e -> new ModificationInventoryMultiple().createInventory(player, CoreSystem.getInstance().getInventoryModificationManager().getMultipleInventories()));
                } else {
                    setItem(InventorySlot.ROW_3_SLOT_5, singleInventoriesItem.create(), coreItemEvent);
                }
            }
        }

        super.openInventory(player);
    }
}
