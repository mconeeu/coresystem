/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ExtendedItemBuilder<M extends ItemMeta> {

    private final ItemStack item;
    protected final M meta;

    @SuppressWarnings("unchecked")
    public ExtendedItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = (M) item.getItemMeta();
    }

    /**
     * returns the final ItemStack
     * @return ItemStack
     */
    public ItemStack getItemStack() {
        item.setItemMeta(meta);
        return item;
    }

    /**
     * returns an ItemBuilder instance with the already set values for further modification
     * @return ItemBuilder instance
     */
    public ItemBuilder toItemBuilder() {
        return ItemBuilder.wrap(getItemStack());
    }

}
