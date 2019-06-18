/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public final class EnchantedBook extends ExtendedItemBuilder<EnchantmentStorageMeta> {

    /**
     * creates new Book instance with item amount 1
     */
    public EnchantedBook() {
        this(1);
    }

    /**
     * creates new Book instance
     * @param amount amount of items in ItemStack
     */
    public EnchantedBook(int amount) {
        super(new ItemStack(Material.ENCHANTED_BOOK, amount));
    }

    private EnchantedBook(ItemStack book) {
        super(book);
    }

    /**
     * wraps an existing ItemStack which must be of Material.ENCHANTED_BOOK in an EnchantedBook object
     * @param book ItemStack
     * @return new EnchantedBook instance
     * @throws ClassCastException if ItemStack has a conflicting Material
     */
    public static EnchantedBook wrap(ItemStack book) throws ClassCastException {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        return new EnchantedBook(book);
    }

    /**
     * Stores the specified enchantment in this item meta
     * @param enchantment Enchantment to store
     * @param level level for the Enchantment
     * @param ignoreLevelRestriction this indicates the enchantment should be applied, ignoring the level limit
     * @return this
     */
    public EnchantedBook addStoredEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction)  {
        meta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    /**
     * Checks for the existence of any stored enchantments.
     * @return true if an enchantment exists on this meta
     */
    public boolean hasStoredEnchants() {
        return meta.hasStoredEnchants();
    }

    /**
     * Checks for storage of the specified enchantment.
     * @param enchantment enchantments to check
     * @return true if this enchantment is stored in this meta
     */
    public boolean hasStoredEnchant(Enchantment enchantment) {
        return meta.hasStoredEnchant(enchantment);
    }

    /**
     * Checks for the level of the stored enchantment.
     * @param enchantment enchantment to check
     * @return The level that the specified stored enchantment has, or 0 if none
     */
    public int getStoredEnchantLevel(Enchantment enchantment) {
        return meta.getStoredEnchantLevel(enchantment);
    }

    /**
     * Gets a copy the stored enchantments in this ItemMeta.
     * @return An immutable copy of the stored enchantments
     */
    public Map<Enchantment, Integer> getStoredEnchants() {
        return meta.getStoredEnchants();
    }

    /**
     * Remove the specified stored enchantment from this item meta.
     * @param enchantment enchantment to remove
     * @return true if the item meta changed as a result of this call, false otherwise
     */
    public boolean removeStoredEnchant(Enchantment enchantment) {
        return meta.removeStoredEnchant(enchantment);
    }

    /**
     * Checks if the specified enchantment conflicts with any enchantments in this ItemMeta.
     * @param enchantment enchantment to test
     * @return true if the enchantment conflicts, false otherwise
     */
    public boolean hasConflictingStoredEnchant(Enchantment enchantment) {
        return meta.hasConflictingStoredEnchant(enchantment);
    }

}
