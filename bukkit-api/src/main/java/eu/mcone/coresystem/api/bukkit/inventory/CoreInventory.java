/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class CoreInventory {

    public static final ItemStack EMPTY_SLOT_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).displayName("§8//§oMCONE§8//").create();

    @Setter
    @Getter
    private String title;
    @Getter
    private int size;
    @Getter
    private Map<Integer, CoreItemStack> items;
    @Setter
    @Getter
    private List<Option> options;

    /**
     * creates new CoreInventory
     *
     * @param title inventory title
     * @param size  inventory size
     * @param args  options
     */
    public CoreInventory(String title, int size, Option... args) {
        this.title = title;
        this.size = size;
        this.items = new HashMap<>();
        this.options = new ArrayList<>(Arrays.asList(args));
    }


    /**
     * creates new CoreInventory
     *
     * @param size inventory size
     * @param args options
     */
    public CoreInventory(int size, Option... args) {
        this.size = size;
        this.items = new HashMap<>();
        this.options = new ArrayList<>(Arrays.asList(args));
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
    public Inventory openInventory(final Player player) {
        CoreSystem.getInstance().getPluginManager().registerCoreInventory(player, this);

        Inventory inventory = Bukkit.createInventory(null, size, title);

        if (options.contains(Option.FILL_EMPTY_SLOTS)) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, EMPTY_SLOT_ITEM);
            }
        }

        for (Map.Entry<Integer, CoreItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }

        player.openInventory(inventory);
        return inventory;
    }

    public enum Option {
        FILL_EMPTY_SLOTS, ENABLE_CLICK_EVENT, CAN_MODIFY
    }

    @Getter
    @AllArgsConstructor
    public static class CoreItemStack {
        private ItemStack itemStack;
        private CoreItemEvent coreItemEvent;
    }
}
