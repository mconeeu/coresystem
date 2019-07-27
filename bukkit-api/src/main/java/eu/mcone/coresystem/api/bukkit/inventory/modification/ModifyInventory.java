/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemStack;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.ItemEventStore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ModifyInventory implements ItemEventStore {

    private final InventoryModificationManager api;

    @Getter
    private final Gamemode gamemode;
    @Getter
    private final String category, name, title;
    @Getter
    private final int size;
    @Getter
    private final List<InventoryOption> options;
    @Getter
    private final Map<Integer, CoreItemStack> items;
    @Getter
    private final Map<UUID, Integer> uniqueItemStacks;
    @Getter
    private final Map<Player, Inventory> inventories;

    /**
     * creates new CoreInventory
     *
     * @param plugin      the target CorePlugin
     * @param name     inventory title
     * @param category category of the inventory
     * @param size     inventory size
     * @param options  options
     */
    public ModifyInventory(CorePlugin plugin, String name, String title, String category, int size, InventoryOption... options) {
        this(plugin.getInventoryModificationManager(), name, title, category, size, options);
    }

    /**
     * creates new CoreInventory
     *
     * @param api      the InventoryModificationManager api
     * @param name     inventory title
     * @param category category of the inventory
     * @param size     inventory size
     * @param options  options
     */
    public ModifyInventory(InventoryModificationManager api, String name, String title, String category, int size, InventoryOption... options) {
        this(api, api.getGamemode(), name, title, category, size, options);
    }

    /**
     * creates new CoreInventory
     *
     * @param api      the InventoryModificationManager api
     * @param name     inventory title
     * @param category category of the inventory
     * @param size     inventory size
     * @param options  options
     */
    public ModifyInventory(InventoryModificationManager api, Gamemode gamemode, String name, String title, String category, int size, InventoryOption... options) {
        this(api, gamemode, null, null, name, title, category, size, options);
    }

    /**
     * creates new CoreInventory
     *
     * @param api      the InventoryModificationManager api
     * @param name     inventory title
     * @param category category of the inventory
     * @param size     inventory size
     * @param options  options
     */
    public ModifyInventory(InventoryModificationManager api, Gamemode gamemode, Map<UUID, Integer> uniqueItemStacks, Map<Integer, CoreItemStack> items, String name, String title, String category, int size, InventoryOption... options) {
        this.api = api;

        this.gamemode = gamemode;
        this.category = category;
        this.name = name;
        this.title = title;
        this.size = size;
        this.options = Arrays.asList(options);

        this.items = items != null ? items : new HashMap<>();
        this.uniqueItemStacks = uniqueItemStacks != null ? uniqueItemStacks : new HashMap<>();
        this.inventories = new HashMap<>();
    }

    public void setItem(int slot, ItemStack item, CoreItemEvent event) {
        items.put(slot, new CoreItemStack(item, event));
        uniqueItemStacks.put(UUID.randomUUID(), slot);
    }

    public void setItem(int slot, ItemStack item) {
        items.put(slot, new CoreItemStack(item, null));
        uniqueItemStacks.put(UUID.randomUUID(), slot);
    }

    public void setItem(int slot, ItemStack item, UUID uuid) {
        items.put(slot, new CoreItemStack(item, null));
        uniqueItemStacks.put(uuid, slot);
    }

    public void openInventory(final Player player) {
        Map<String, UUID> modifiedInventoryItems = api.getModifiedInventoryItems(player.getUniqueId(), this);
        Map<Integer, ItemStack> modifiedItems = new HashMap<>();

        //Check if Inventory is modified
        if (modifiedInventoryItems != null) {
            for (Map.Entry<String, UUID> modifiedEntry : modifiedInventoryItems.entrySet()) {
                modifiedItems.put(Integer.valueOf(modifiedEntry.getKey()), items.get(uniqueItemStacks.get(modifiedEntry.getValue())).getItemStack());
            }

            openInventory(player, title, size, items, modifiedItems);
        } else {
            openInventory(player, title, size, items, Collections.emptyMap());
        }
    }

    public Inventory getCurrentInventory(Player player) {
        return inventories.getOrDefault(player, null);
    }

    private void openInventory(Player player, String title, int size, Map<Integer, CoreItemStack> items, Map<Integer, ItemStack> modifiedItems) {
        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (Map.Entry<Integer, CoreItemStack> item : items.entrySet()) {
            inventory.setItem(
                    modifiedItems.containsValue(item.getValue().getItemStack()) ? getModifiedSlotForItem(modifiedItems, item.getValue().getItemStack()) : item.getKey(),
                    item.getValue().getItemStack()
            );
        }

        inventories.put(player, inventory);
        player.openInventory(inventory);
    }

    private int getModifiedSlotForItem(Map<Integer, ItemStack> modifiedItems, ItemStack itemStack) {
        for (Map.Entry<Integer, ItemStack> entry : modifiedItems.entrySet()) {
            if (entry.getValue().equals(itemStack)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public UUID getUuidForSlot(int slot) {
        for (Map.Entry<UUID, Integer> uuidIntegerEntry : uniqueItemStacks.entrySet()) {
            if (uuidIntegerEntry.getValue().equals(slot)) {
                return uuidIntegerEntry.getKey();
            }
        }
        return null;
    }

}
