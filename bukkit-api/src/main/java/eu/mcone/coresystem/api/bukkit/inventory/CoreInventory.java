/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CoreInventory implements ItemEventStore {

    public static final String PLACEHOLDER_ITEM_DISPLAYNAME = "§8//§oMCONE§8//";
    public static final ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData()).displayName(PLACEHOLDER_ITEM_DISPLAYNAME).create();
    public static final ItemStack BACK_ITEM = new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück").create();

    @Getter
    protected final Player player;
    @Getter
    protected final Inventory inventory;
    @Getter
    protected Map<Integer, CoreItemStack> items;
    @Getter
    private final boolean allowModification;

    /**
     * creates new CoreInventory
     *
     * @param title inventory title
     * @param size  inventory size
     * @param options  options
     */
    public CoreInventory(String title, Player player, int size, InventoryOption... options) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.items = new HashMap<>();
        this.allowModification = Arrays.asList(options).contains(InventoryOption.ALLOW_MODIFICATION);

        if (Arrays.asList(options).contains(InventoryOption.FILL_EMPTY_SLOTS)) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, PLACEHOLDER_ITEM);
            }
        }
    }

    /**
     * sets an item in the inventory
     *
     * @param slot  inventory slot
     * @param item  item stack
     * @param event event, called when player clicks on the item
     */
    public void setItem(int slot, ItemStack item, CoreItemEvent event) {
        items.put(slot, new CoreItemStack(item, event));
        inventory.setItem(slot, item);
    }

    /**
     * sets an item in the inventory
     *
     * @param slot inventory slot
     * @param item item stack
     */
    public void setItem(int slot, ItemStack item) {
        items.put(slot, new CoreItemStack(item, null));
        inventory.setItem(slot, item);
    }

    /**
     * opens the inventory
     */
    public Inventory openInventory() {
        CoreSystem.getInstance().getPluginManager().registerCoreInventory(player, this);

        player.openInventory(inventory);
        return inventory;
    }

}
