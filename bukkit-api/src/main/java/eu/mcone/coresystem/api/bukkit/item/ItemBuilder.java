/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private List<String> lore;

    /**
     * create ItemBuilder
     * @param material material
     */
    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    /**
     * create ItemBuilder
     * @param material material
     * @param amount amount of items in ItemStack
     */
    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public static ItemBuilder wrap(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            throw new IllegalStateException("missing ItemMeta!");
        } else {
            return new ItemBuilder(itemStack.clone());
        }
    }

    /**
     * change displayname of the item
     * @param displayName displayname
     * @return this
     */
    public ItemBuilder displayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    /**
     * set loren of the item
     * @param lore loren (Array)
     * @return this
     */
    public ItemBuilder lore(String... lore) {
        this.lore = new ArrayList<>(Arrays.asList(lore));
        return this;
    }

    /**
     * set loren of the item
     * @param loren loren (ArrayList)
     * @return this
     */
    public ItemBuilder lore(List<String> loren) {
        this.lore = loren;
        return this;
    }

    /**
     * add a lore of the item
     * @param lore lore (String)
     * @return this
     */
    public ItemBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    /**
     * add enchantment
     * @param enchantment enchantment
     * @param level level of enchantment
     * @return this
     */
    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * add enchantment
     * @param enchantments Map of enchantments
     * @return this
     */
    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            itemMeta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
        }
        return this;
    }

    /**
     * add ItemFlags
     * @param flags item flags (Array)
     * @return this
     */
    public ItemBuilder itemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    /**
     * set if item should be unbreakable
     * @param unbreakable boolean unbreakable
     * @return this
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * returns a new ItemBuilder with a fresh ItemStack instance
     * @return new ItemBuilder
     */
    public ItemBuilder clone() {
        ItemStack item = itemStack.clone();
        item.setItemMeta(itemMeta);

        return ItemBuilder.wrap(item);
    }

    /**
     * create ItemStack
     * @return ItemStack
     */
    public ItemStack create() {
        if (lore != null && !lore.isEmpty())
            itemMeta.setLore(this.lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
