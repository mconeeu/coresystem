/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public final class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    /**
     * create ItemBuilder
     * @param material material
     */
    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
    }

    /**
     * create ItemBuilder
     * @param material material
     * @param amount amount of items in ItemStack
     */
    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
    }

    /**
     * create ItemBuilder
     * @param material material
     * @param amount amount of items in ItemStack
     * @param subId sub ID of the material
     */
    public ItemBuilder(Material material, int amount, int subId) {
        itemStack = new ItemStack(material, amount, (short) subId);
        itemMeta = itemStack.getItemMeta();
    }

    /**
     * create ItemBuilder with short sub ID
     * @param material material
     * @param amount amount of items in ItemStack
     * @param subId sub ID of the material
     */
    public ItemBuilder(Material material, int amount, short subId) {
        itemStack = new ItemStack(material, amount, subId);
        itemMeta = itemStack.getItemMeta();
    }

    /**
     * create ItemBuilder for SkullItem by owner
     * @param owner owner of the skull
     * @param amount amount of items in ItemStack
     * @return new ItemBuilder
     */
    public static ItemBuilder createSkullItem(String owner, int amount) {
        ItemBuilder factory = new ItemBuilder(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());
        ((SkullMeta) factory.itemMeta).setOwner(owner);

        return factory;
    }

    /**
     * create ItemBuilder for SkullItem by predefined Skin
     * @param skin BCS SkinInfo object
     * @param amount amount of items in ItemStack
     * @return new ItemBuilder
     */
    public static ItemBuilder createSkullItem(SkinInfo skin, int amount) {
        ItemBuilder factory = new ItemBuilder(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        setProfileField(factory.itemMeta, profile);
        return factory;
    }

    /**
     * create ItemBuilder for SkullItem by URL
     * @param url url of skin.png
     * @param amount amount of items in ItemStack
     * @return new ItemBuilder
     */
    public static ItemBuilder createSkullItemFromURL(String url, int amount) {
        ItemBuilder factory = new ItemBuilder(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        setProfileField(factory.itemMeta, profile);
        return factory;
    }

    /**
     * create ItemBuilder for LeatherArmor item
     * @param material Material of LeatherArmor item
     * @param color color of leather item
     * @return new ItemBuilder
     */
    public static ItemBuilder createLeatherArmorItem(Material material, Color color) {
        ItemBuilder factory = new ItemBuilder(material, 1, (short) 0);
        ((LeatherArmorMeta) factory.itemMeta).setColor(color);

        return factory;
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
     * set lores of the item
     * @param lore lores (Array)
     * @return this
     */
    public ItemBuilder lore(String... lore) {
        itemMeta.setLore(new ArrayList<>(Arrays.asList(lore)));
        return this;
    }

    /**
     * set lores of the item
     * @param lores lores (ArrayList)
     * @return this
     */
    public ItemBuilder lore(List<String> lores) {
        itemMeta.setLore(lores);
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
        itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    /**
     * create ItemStack
     * @return ItemStack
     */
    public ItemStack create() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static void setProfileField(ItemMeta meta, GameProfile profile) {
        Field profileField;

        try {
            profileField = ((SkullMeta) meta).getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
