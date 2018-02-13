/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemFactory {

    public static ItemStack createItem(Material material, int subid, int amount, String displayname, boolean unbreakable){
        ItemStack item = new ItemStack(material, amount, (short)subid);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createItem(Material material, int subid, int amount, String displayname, List<String> lore, boolean unbreakable){
        ItemStack item = new ItemStack(material, amount, (short)subid);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }


    public static ItemStack createEnchantedItem(Material Material, Enchantment enchantment, int level, int subid, int amount, String displayname, boolean unbreakable) {

        ItemStack item = new ItemStack(Material, amount, (short) subid);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.addEnchant(enchantment, level, true);
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createEnchantedItem(Material Material, Enchantment enchantment, int level, int subid, int amount, String displayname, List<String> lore, boolean unbreakable) {

        ItemStack item = new ItemStack(Material, amount, (short) subid);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        itemMeta.addEnchant(enchantment, level, true);
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createEnchantedItem(Material Material, HashMap<Enchantment, Integer> enchantments, int subid, int amount, String displayname, boolean unbreakable) {

        ItemStack item = new ItemStack(Material, amount, (short) subid);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createEnchantedItem(Material Material, HashMap<Enchantment, Integer> enchantments, int subid, int amount, String displayname, List<String> lore, boolean unbreakable) {

        ItemStack item = new ItemStack(Material, amount, (short) subid);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayname);
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }



    public static ItemStack createSkullItem(String displayname, String owner, int amount, List<String> lore) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();

        itemMeta.setOwner(owner);
        itemMeta.setDisplayName(displayname);
        itemMeta.spigot().setUnbreakable(true);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createCustomSkullItem(String displayname, String url, int amount, List<String> lore) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, amount, (short)3);

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(displayname);
        headMeta.spigot().setUnbreakable(true);
        headMeta.setLore(lore);
        headMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        headMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;

        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        head.setItemMeta(headMeta);
        return head;
    }

}
