/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.bukkit.inventory.modification.CoreInventoryModificationManager;
import org.bukkit.entity.Player;

public class ModificationGamemodeInventory extends CoreInventory {

    private static final Gamemode[] MODIFIED_INVENTORY_GAMEMODES = new Gamemode[]{Gamemode.SKYPVP, Gamemode.KNOCKIT};

    public ModificationGamemodeInventory(CoreInventoryModificationManager api, Player p) {
        super("§8» §a§lModifizierte Inventare", p, InventorySlot.ROW_3, InventoryOption.FILL_EMPTY_SLOTS);

        int i = InventorySlot.ROW_2_SLOT_4;
        for (Gamemode gamemode : MODIFIED_INVENTORY_GAMEMODES) {
            int inventories = api.getModifyInventories(gamemode).size();
            CoreItemEvent event = inventories > 0 ? e -> new ModificationCategoryInventory(api, p, gamemode).openInventory() : null;

            setItem(
                    i,
                    new ItemBuilder(gamemode.getItem()).displayName(gamemode.getColor() + gamemode.getName()).lore("§7§o"+inventories+" modifizierbare Inventare").create(),
                    event
            );
            i += 2;
        }
    }

}
