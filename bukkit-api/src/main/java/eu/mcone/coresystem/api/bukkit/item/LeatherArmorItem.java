/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class LeatherArmorItem extends ExtendedItemBuilder<LeatherArmorMeta> {

    /**
     * creates new LeatherArmorItem instance with amount 1
     * @param armorType type of leather armor, must be of Material type LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS
     */
    public LeatherArmorItem(Material armorType) {
        this(armorType, 1);
    }

    /**
     * creates new LeatherArmorItem instance
     * @param armorType type of leather armor, must be of Material type LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS
     * @param amount amount of items in ItemStack
     * @param color color of the armor item
     */
    public LeatherArmorItem(Material armorType, int amount, Color color) {
        this(armorType, amount);
        meta.setColor(color);
    }

    private LeatherArmorItem(ItemStack item) {
        super(item);
    }

    /**
     * wraps an existing ItemStack which must be of Material type LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS
     * @param armorItem ItemStack
     * @return new LeatherArmorItem instance
     * @throws ClassCastException if ItemStack has a conflicting Material
     */
    public static LeatherArmorItem wrap(ItemStack armorItem) throws ClassCastException {
        LeatherArmorMeta meta = (LeatherArmorMeta) armorItem.getItemMeta();
        return new LeatherArmorItem(armorItem);
    }

    /**
     * creates new LeatherArmorItem instance
     * @param armorType type of leather armor, must be of Material type LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS
     * @param amount amount of items in ItemStack
     */
    public LeatherArmorItem(Material armorType, int amount) {
        super(new ItemStack(armorType, amount));
    }

    /**
     * Sets the color of the armor.
     * @param color the color to set. Setting it to null is equivalent to setting it to
     * @return this
     */
    public LeatherArmorItem setColor(Color color) {
        meta.setColor(color);
        return this;
    }

    /**
     * Gets the color of the armor. If it has not been set otherwise, it will be ItemFactory.getDefaultLeatherColor().
     * @return this
     */
    public Color getColor() {
        return meta.getColor();
    }

}
