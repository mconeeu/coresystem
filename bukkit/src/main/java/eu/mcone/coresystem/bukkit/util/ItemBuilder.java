/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.lib.player.Skin;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material, int amount, int subId) {
        itemStack = new ItemStack(material, amount, (short) subId);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int amount, short subId) {
        itemStack = new ItemStack(material, amount, subId);
        itemMeta = itemStack.getItemMeta();
    }

    public static ItemBuilder createSkullItem(String owner, int amount) {
        ItemBuilder factory = new ItemBuilder(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());
        ((SkullMeta) factory.itemMeta).setOwner(owner);

        return factory;
    }

    public static ItemBuilder createSkullItem(Skin skin, int amount) {
        ItemBuilder factory = new ItemBuilder(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        Field profileField;

        try {
            profileField = ((SkullMeta) factory.itemMeta).getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(factory.itemMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        return factory;
    }

    public static ItemBuilder createSkullItemFromURL(String url, int amount) {
        ItemBuilder factory = new ItemBuilder(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;

        try {
            profileField = ((SkullMeta) factory.itemMeta).getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(factory.itemMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        return factory;
    }

    public ItemBuilder displayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        itemMeta.setLore(new ArrayList<>(Arrays.asList(lore)));
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder itemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder unbreakable() {
        itemMeta.spigot().setUnbreakable(true);
        return this;
    }

    public ItemStack create() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
