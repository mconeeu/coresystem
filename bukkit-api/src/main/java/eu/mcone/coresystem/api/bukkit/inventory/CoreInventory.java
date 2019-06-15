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

import java.util.*;

public abstract class CoreInventory {

    public static final ItemStack EMPTY_SLOT_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).displayName("§8//§oMCONE§8//").create();

    @Getter
    private String name;
    @Getter
    private Inventory inventory;
    @Getter
    private Player player;
    @Getter
    private Map<ItemStack, CoreItemEvent> events;
    @Getter
    private Option[] options;

    /**
     * creates new CoreInventory
     * @param name inventory title
     * @param player target player
     * @param size inventory size
     * @param args options
     */
    protected CoreInventory(String name, Player player, int size, Option... args) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, size, name);
        this.player = player;
        this.events = new HashMap<>();
        this.options = args;

        CoreSystem.getInstance().getPluginManager().registerCoreInventory(this, player);

        List<Option> options = new ArrayList<>(Arrays.asList(args));
        if (options.contains(Option.FILL_EMPTY_SLOTS)) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, EMPTY_SLOT_ITEM);
            }
        }
    }

    /**
     * sets an item in the inventory
     * @param slot inventory slot
     * @param item item stack
     * @param event event, called when player clicks on the item
     */
    public void setItem(int slot, ItemStack item, CoreItemEvent event) {
        inventory.setItem(slot, item);
        events.put(item, event);
    }

    /**
     * sets an item in the inventory
     * @param slot inventory slot
     * @param item item stack
     */
    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    /**
     * opens the inventory
     */
    protected void openInventory() {
        player.openInventory(inventory);
    }

    public enum Option {
        FILL_EMPTY_SLOTS, ENABLE_CLICK_EVENT
    }

}
