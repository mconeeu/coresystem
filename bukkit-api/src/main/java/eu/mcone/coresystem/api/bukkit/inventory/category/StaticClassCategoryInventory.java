/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.category;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class StaticClassCategoryInventory extends CategoryInventory {

    private final Map<ItemStack, Class<? extends StaticClassCategoryInventory>> categoryInventories;

    public StaticClassCategoryInventory(String title, Player player) {
        super(title, player);
        this.categoryInventories = new HashMap<>();
    }

    public void addCategoryWithInventoryClass(ItemStack itemStack, Class<? extends StaticClassCategoryInventory> categoryClass) {
        categoryInventories.put(itemStack, categoryClass);
        addCategory(itemStack);
    }

    @Override
    public Inventory openInventory(int itemPage) {
        setCurrentCategoryItem(getCurrentCategoryItem());
        return super.openInventory(itemPage);
    }

    @Override
    protected void openCategoryInventory(ItemStack categoryItem, Player player) {
        try {
            categoryInventories.get(categoryItem).getConstructor(Player.class).newInstance(player).openInventory();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected ItemStack getCurrentCategoryItem() {
        ItemStack item = null;

        for (Map.Entry<ItemStack, Class<? extends StaticClassCategoryInventory>> itemStackClassEntry : categoryInventories.entrySet()) {
            if (itemStackClassEntry.getValue() != null) {
                if (itemStackClassEntry.getValue().equals(getClass())) {
                    item = itemStackClassEntry.getKey();
                    break;
                }
            }
        }

        return item;
    }

}
