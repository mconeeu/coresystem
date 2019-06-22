/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CoreInventory implements ItemEventStore {

    public static final ItemStack PLACEHOLDER_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).displayName("§8//§oMCONE§8//").create();

    @Getter
    protected final Player player;
    @Getter
    protected final Inventory inventory;
    @Getter
    protected Map<Integer, CoreItemStack> items;

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
    }

    /**
     * sets an item in the inventory
     *
     * @param slot inventory slot
     * @param item item stack
     */
    public void setItem(int slot, ItemStack item) {
        items.put(slot, new CoreItemStack(item, null));
    }

    /**
     * opens the inventory
     */
    public Inventory openInventory() {
        CoreSystem.getInstance().getPluginManager().registerCoreInventory(player, this);

        for (Map.Entry<Integer, CoreItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }

        player.openInventory(inventory);
        return inventory;
    }

}
