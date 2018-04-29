/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class CoreInventory {

    @Getter
    protected Inventory inventory;
    @Getter
    protected Player player;
    @Getter
    private Map<ItemStack, CoreItemEvent> events;

    public CoreInventory(String name, Player player, int size, Option... args) {
        this.inventory = Bukkit.createInventory(null, size, name);
        this.player = player;
        this.events = new HashMap<>();

        CoreSystem.getInstance().registerInventory(this);

        List<Option> options = new ArrayList<>(Arrays.asList(args));
        if (options.contains(Option.FILL_EMPTY_SLOTS)) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).displayName("§8//§oMCONE§8//").create());
            }
        }
    }

    public CoreInventory(String name, Player player, CoreInventorySize size, Option... args) {
        this.inventory = Bukkit.createInventory(null, size.getValue(), name);
        this.player = player;
        this.events = new HashMap<>();

        CoreSystem.getInstance().registerInventory(this);

        List<Option> options = new ArrayList<>(Arrays.asList(args));
        if (options.contains(Option.FILL_EMPTY_SLOTS)) {
            for (int i = 0; i < size.getValue(); i++) {
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).displayName("§8//§oMCONE§8//").create());
            }
        }
    }

    public void setItem(int slot, ItemStack item, CoreItemEvent event) {
        inventory.setItem(slot, item);
        events.put(item, event);

    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    protected void openInventory() {
        player.openInventory(inventory);
    }

    public enum Option {
        FILL_EMPTY_SLOTS
    }

}
