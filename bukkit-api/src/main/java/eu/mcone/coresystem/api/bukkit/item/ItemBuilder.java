/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public final class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private List<String> lore;

    /**
     * create ItemBuilder
     *
     * @param material material
     */
    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    /**
     * create ItemBuilder
     *
     * @param material material
     * @param amount   amount of items in ItemStack
     */
    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    /**
     * create ItemBuilder
     *
     * @param material material
     * @param amount   amount of items in ItemStack
     * @param subId    sub ID of the material
     */
    public ItemBuilder(Material material, int amount, int subId) {
        itemStack = new ItemStack(material, amount, (short) subId);
        itemMeta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    /**
     * create ItemBuilder with short sub ID
     *
     * @param material material
     * @param amount   amount of items in ItemStack
     * @param subId    sub ID of the material
     */
    public ItemBuilder(Material material, int amount, short subId) {
        itemStack = new ItemStack(material, amount, subId);
        itemMeta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            this.lore = itemMeta.getLore();
        }
    }

    public static ItemBuilder wrap(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            throw new IllegalStateException("missing ItemMeta!");
        } else {
            return new ItemBuilder(itemStack.clone());
        }
    }

    /**
     * create ItemBuilder for LeatherArmor item
     *
     * @param material Material of LeatherArmor item
     * @param color    color of leather item
     * @return new ItemBuilder
     */
    public static ItemBuilder createLeatherArmorItem(Material material, Color color) {
        ItemBuilder factory = new ItemBuilder(material, 1, (short) 0);
        ((LeatherArmorMeta) factory.itemMeta).setColor(color);

        return factory;
    }

    /**
     * change displayname of the item
     *
     * @param displayName displayname
     * @return this
     */
    public ItemBuilder displayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    /**
     * set loren of the item
     *
     * @param lore loren (Array)
     * @return this
     */
    public ItemBuilder lore(String... lore) {
        this.lore = new ArrayList<>(Arrays.asList(lore));
        return this;
    }

    /**
     * set loren of the item
     *
     * @param loren loren (ArrayList)
     * @return this
     */
    public ItemBuilder lore(List<String> loren) {
        this.lore = loren;
        return this;
    }

    /**
     * add a lore of the item
     *
     * @param lore lore (String)
     * @return this
     */
    public ItemBuilder addLore(String lore) {
        if (this.lore != null) {
            this.lore.add(lore);
        } else {
            this.lore = new ArrayList<>(Collections.singleton(lore));
        }
        return this;
    }

    /**
     * add enchantment
     *
     * @param enchantment enchantment
     * @param level       level of enchantment
     * @return this
     */
    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * add enchantment
     *
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
     *
     * @param flags item flags (Array)
     * @return this
     */
    public ItemBuilder itemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    /**
     * set if item should be unbreakable
     *
     * @param unbreakable boolean unbreakable
     * @return this
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    /**
     * returns a new ItemBuilder with a fresh ItemStack instance
     *
     * @return new ItemBuilder
     */
    public ItemBuilder clone() {
        ItemStack item = itemStack.clone();
        item.setItemMeta(itemMeta);

        return ItemBuilder.wrap(item);
    }

    /**
     * create ItemStack
     *
     * @return ItemStack
     */
    public ItemStack create() {
        if (lore != null && !lore.isEmpty())
            itemMeta.setLore(this.lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
