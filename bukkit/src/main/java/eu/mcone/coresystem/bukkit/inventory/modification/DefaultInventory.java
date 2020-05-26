/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemStack;
import eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager;
import eu.mcone.coresystem.api.bukkit.inventory.modification.ModifyInventory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class DefaultInventory extends BasicInventory {

    private byte[] defaultItemsAsByteArray;
    private transient Map<Integer, UniqueItemStack> defaultItems;

    public DefaultInventory(final long lastUpdate, final Gamemode gamemode, final String category, final String name, final String title, final int size, final Map<Integer, UniqueItemStack> defaultItems) {
        super(lastUpdate, gamemode, category, name, title, size);
        this.defaultItemsAsByteArray = CoreInventoryModificationManager.toByteArray(defaultItems);
    }

    public Map<Integer, UniqueItemStack> calculateDefaultItems() {
        return defaultItems != null ? defaultItems : (defaultItems = CoreInventoryModificationManager.fromByteArray(defaultItemsAsByteArray));
    }

    public ModifyInventory toModifyInventory(InventoryModificationManager api) {
        Map<UUID, Integer> uniqueItemStacks = new HashMap<>();
        Map<Integer, CoreItemStack> items = new HashMap<>();
        for (Map.Entry<Integer, UniqueItemStack> entry : calculateDefaultItems().entrySet()) {
            uniqueItemStacks.put(entry.getValue().getUuid(), entry.getKey());
            items.put(entry.getKey(), new CoreItemStack(entry.getValue().getItemStack(), null));
        }

        return new ModifyInventory(api, getGamemode(), uniqueItemStacks, items, getName(), getTitle(), getCategory(), getSize()) {
        };
    }

    @Override
    public String toString() {
        return "DefaultInventory(" + getGamemode() + "." + getCategory() + "." + getName() + ")";
    }

}
