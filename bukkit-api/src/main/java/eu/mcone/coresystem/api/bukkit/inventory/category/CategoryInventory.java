/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.category;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class CategoryInventory extends CoreInventory {

    private static final int[] CATEGORY_SLOTS = new int[]{InventorySlot.ROW_1_SLOT_1, InventorySlot.ROW_2_SLOT_1, InventorySlot.ROW_3_SLOT_1, InventorySlot.ROW_4_SLOT_1, InventorySlot.ROW_5_SLOT_1, InventorySlot.ROW_6_SLOT_1};

    public static final int MAX_PAGE_SIZE = 18;
    public static final ItemStack UP_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/a156b31cbf8f774547dc3f9713a770ecc5c727d967cb0093f26546b920457387", 1).toItemBuilder().displayName("§f§lNach Oben").lore("", "§8» §f§nLinksklick§8 | §7§oMehr Anzeigen").create();
    public static final ItemStack DOWN_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/fe3d755cecbb13a39e8e9354823a9a02a01dce0aca68ffd42e3ea9a9d29e2df2", 1).toItemBuilder().displayName("§f§lNach Unten").lore("", "§8» §f§nLinksklick§8 | §7§oMehr Anzeigen").create();
    public static final ItemStack LEFT_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23", 1).toItemBuilder().displayName("§7Vorherige Seite").lore("", "§8» §f§nLinksklick§8 | §7§oMehr Anzeigen").create();
    public static final ItemStack RIGHT_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b", 1).toItemBuilder().displayName("§7Nächste Seite").lore("", "§8» §f§nLinksklick§8 | §7§oMehr Anzeigen").create();
    public static final ItemStack REFRESH_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61", 1).toItemBuilder().displayName("§7Neu laden").lore("", "§8» §f§nLinksklick§8 | §7§oMehr Anzeigen").create();

    private final LinkedList<ItemStack> categories;
    private final List<CategoryInvItem> categoryInvItems;
    protected ItemStack currentCategoryItem;

    public CategoryInventory(String title, Player player, ItemStack currentCategoryItem) {
        super(title, player, InventorySlot.ROW_6);

        this.categories = new LinkedList<>();
        this.categoryInvItems = new ArrayList<>();
        this.currentCategoryItem = currentCategoryItem;
    }

    CategoryInventory(String title, Player player, InventoryOption... options) {
        super(title, player, InventorySlot.ROW_6, options);

        this.categories = new LinkedList<>();
        this.categoryInvItems = new ArrayList<>();
    }

    public void addCategory(ItemStack item) {
        categories.add(item);
    }

    public void addItem(ItemStack item, CoreItemEvent event) {
        categoryInvItems.add(new CategoryInvItem(item, event));
    }

    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    protected abstract void openCategoryInventory(ItemStack categoryItem, Player player);

    public Inventory openInventory(int itemPage) {
        items.clear();
        inventory.clear();
        setPlaceholders();

        final int categoryPages = calculateCategoryPages();
        final int categoryPage = getCategoryPageOfItem(currentCategoryItem);
        final List<ItemStack> categoryItems = getCategoryPageItems(categoryPages, categoryPage);

        for (int i = 0; i < CATEGORY_SLOTS.length && i < categoryItems.size(); i++) {
            ItemStack categoryItem = categoryItems.get(i);

            if (categoryItem.equals(UP_ITEM)) {
                int catItemIndex = categoryPage - 1 == 1 ? 0 : 1;
                setItem(CATEGORY_SLOTS[i], categoryItem, e -> openCategoryInventory(getCategoryPageItems(categoryPages, categoryPage - 1).get(catItemIndex), player));
            } else if (categoryItem.equals(DOWN_ITEM)) {
                setItem(CATEGORY_SLOTS[i], categoryItem, e -> openCategoryInventory(getCategoryPageItems(categoryPages, categoryPage + 1).get(1), player));
            } else if (categoryItem.equals(currentCategoryItem)) {
                setItem(CATEGORY_SLOTS[i], ItemBuilder.wrap(categoryItem).enchantment(Enchantment.DAMAGE_ALL, 1).itemFlags(ItemFlag.HIDE_ENCHANTS).create());
            } else {
                setItem(CATEGORY_SLOTS[i], categoryItem, e -> openCategoryInventory(categoryItem, player));
            }
        }

        int startItem = ((itemPage - 1) * MAX_PAGE_SIZE), endItem = startItem + MAX_PAGE_SIZE;
        int allItemsSize = setPaginatedItems(startItem, endItem, categoryInvItems);

        for (int i = startItem, x = 11; i < endItem && i < allItemsSize; i++, x++) {
            if (x == 17) x = 20;
            else if (x == 26) x = 29;

            CategoryInvItem item = categoryInvItems.get(i);
            setItem(x, item.getItemStack(), item.getItemEvent());
        }

        final int itemPages = (allItemsSize / MAX_PAGE_SIZE) + (((allItemsSize % MAX_PAGE_SIZE) > 0) ? 1 : 0);
        setItem(InventorySlot.ROW_6_SLOT_5, LEFT_ITEM, e -> {
            if (itemPage >= 2) {
                openInventory(itemPage - 1);
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
            } else {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            }
        });
        setItem(InventorySlot.ROW_6_SLOT_6, RIGHT_ITEM, e -> {
            if (itemPage < itemPages) {
                openInventory(itemPage + 1);
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
            } else {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
            }
        });

        return super.openInventory();
    }

    public int setPaginatedItems(int skip, int limit, List<CategoryInvItem> items) {
        return categoryInvItems.size();
    }

    @Override
    public Inventory openInventory() {
        return openInventory(1);
    }

    private List<ItemStack> getCategoryPageItems(int allPages, int currentPage) {
        List<ItemStack> categoryItems = new ArrayList<>();
        if (allPages > 1 && currentPage != 1) {
            categoryItems.add(UP_ITEM);
        }

        int pages = 0;
        int rowSize = categories.size() <= 6 ? 6 : 5;

        Iterator<ItemStack> categoryIterator = categories.iterator();
        for (int i = 1, x = 1; i <= categories.size(); i++, x++) {
            if (x == 1) {
                ++pages;
            }

            ItemStack currentCategoryItem = categoryIterator.next();
            if (pages == currentPage) {
                categoryItems.add(currentCategoryItem);
            } else if (pages > currentPage) {
                break;
            }

            if (x == rowSize) {
                if (rowSize == 4 && i + 1 == categories.size()) {
                    rowSize = 5;
                } else {
                    x = 0;

                    if (rowSize == 5) {
                        rowSize = 4;
                    }
                }
            }
        }

        if (currentPage != allPages) {
            categoryItems.add(DOWN_ITEM);
        }

        return categoryItems;
    }

    private int getCategoryPageOfItem(ItemStack categoryItem) {
        Iterator<ItemStack> categoryIterator = categories.iterator();
        int page = 0;
        int rowSize = categories.size() <= 6 ? 6 : 5;

        for (int i = 1, x = 1; i <= categories.size(); i++, x++) {
            if (x == 1) {
                ++page;
            }

            if (categoryIterator.next().equals(categoryItem)) {
                break;
            }

            if (x == rowSize) {
                if (rowSize == 4 && i + 1 == categories.size()) {
                    rowSize = 5;
                } else {
                    x = 0;

                    if (rowSize == 5) {
                        rowSize = 4;
                    }
                }
            }
        }

        return page;
    }

    private int calculateCategoryPages() {
        int pages = 0;

        int rowSize = categories.size() <= 6 ? 6 : 5;
        for (int i = 1, x = 1; i <= categories.size(); i++, x++) {
            if (x == 1) {
                ++pages;
            }

            if (x == rowSize) {
                if (rowSize == 4 && i + 1 == categories.size()) {
                    rowSize = 5;
                } else {
                    x = 0;

                    if (rowSize == 5) {
                        rowSize = 4;
                    }
                }
            }
        }

        return pages;
    }

    private void setPlaceholders() {
        setItem(1, CoreInventory.PLACEHOLDER_ITEM);
        setItem(2, CoreInventory.PLACEHOLDER_ITEM);
        setItem(3, CoreInventory.PLACEHOLDER_ITEM);
        setItem(4, CoreInventory.PLACEHOLDER_ITEM);
        setItem(5, CoreInventory.PLACEHOLDER_ITEM);
        setItem(6, CoreInventory.PLACEHOLDER_ITEM);
        setItem(7, CoreInventory.PLACEHOLDER_ITEM);
        setItem(8, CoreInventory.PLACEHOLDER_ITEM);

        setItem(10, CoreInventory.PLACEHOLDER_ITEM);
        setItem(17, CoreInventory.PLACEHOLDER_ITEM);

        setItem(19, CoreInventory.PLACEHOLDER_ITEM);
        setItem(26, CoreInventory.PLACEHOLDER_ITEM);

        setItem(28, CoreInventory.PLACEHOLDER_ITEM);
        setItem(35, CoreInventory.PLACEHOLDER_ITEM);

        setItem(37, CoreInventory.PLACEHOLDER_ITEM);
        setItem(38, CoreInventory.PLACEHOLDER_ITEM);
        setItem(39, CoreInventory.PLACEHOLDER_ITEM);
        setItem(40, CoreInventory.PLACEHOLDER_ITEM);
        setItem(41, CoreInventory.PLACEHOLDER_ITEM);
        setItem(42, CoreInventory.PLACEHOLDER_ITEM);
        setItem(43, CoreInventory.PLACEHOLDER_ITEM);
        setItem(44, CoreInventory.PLACEHOLDER_ITEM);

        setItem(46, CoreInventory.PLACEHOLDER_ITEM);
        setItem(47, CoreInventory.PLACEHOLDER_ITEM);
        setItem(48, CoreInventory.PLACEHOLDER_ITEM);

        setItem(51, CoreInventory.PLACEHOLDER_ITEM);
        setItem(52, CoreInventory.PLACEHOLDER_ITEM);
        setItem(53, CoreInventory.PLACEHOLDER_ITEM);
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class CategoryInvItem {
        private ItemStack itemStack;
        private CoreItemEvent itemEvent;
    }

    void setCurrentCategoryItem(ItemStack currentCategoryItem) {
        this.currentCategoryItem = currentCategoryItem;
    }

}
