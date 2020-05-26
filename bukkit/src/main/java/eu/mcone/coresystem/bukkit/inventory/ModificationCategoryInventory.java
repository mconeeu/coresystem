/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.bukkit.inventory.modification.CoreInventoryModificationManager;
import eu.mcone.coresystem.bukkit.inventory.modification.ModifiedInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ModificationCategoryInventory extends CategoryInventory {

    private final CoreInventoryModificationManager api;
    private final Gamemode gamemode;

    public ModificationCategoryInventory(CoreInventoryModificationManager api, Player player, Gamemode gamemode, String category) {
        super(gamemode.getLabel(), player, makeItem(category));
        this.api = api;
        this.gamemode = gamemode;

        for (String gamemodeCategory : api.getModifyInventoryCategories(gamemode)) {
            ItemStack item = makeItem(gamemodeCategory);
            addCategory(item);
        }

        for (ModifyInventory inv : api.getModifyInventories(gamemode, category)) {
            ModifiedInventory modifiedInventory = api.getModifiedInventory(player.getUniqueId(), inv);

            addItem(new ItemBuilder(Material.CHEST).displayName(inv.getTitle()).lore(
                    modifiedInventory != null
                            ? "§7§oZuletzt bearbeitet: " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(modifiedInventory.getLastUpdate() * 1000))
                            : "§7§oNoch nie bearbeitet"
            ).create(), e -> api.setCurrentlyModifying(player, inv).openInventory(player));
        }

        openInventory();
    }

    public ModificationCategoryInventory(CoreInventoryModificationManager api, Player player, Gamemode gamemode) {
        this(api, player, gamemode, api.getModifyInventoryCategories(gamemode).iterator().next());
    }

    @Override
    protected void openCategoryInventory(ItemStack categoryItem, Player player) {
        new ModificationCategoryInventory(api, player, gamemode, categoryItem.getItemMeta().getDisplayName().replaceAll("§f§l", ""));
    }

    private static ItemStack makeItem(String category) {
        return new ItemBuilder(Material.BOOKSHELF).displayName("§f§l" + category).lore("§7§oKategorie von", "§7§omodifizierten Inventaren").create();
    }

}
