/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config.typeadapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackTypeAdapterUtils {

    @SuppressWarnings("unchecked")
    public static BookMeta getBookMeta(Map<String, Object> data) {
        ItemStack dummyItems = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) dummyItems.getItemMeta();
        String title = null, author = null;
        List<String> pages = null;

        if (data.containsKey("title")) {
            title = (String) data.get("title");
        }
        if (data.containsKey("author")) {
            author = (String) data.get("author");
        }
        if (data.containsKey("pages")) {
            pages = (ArrayList<String>) data.get("pages");
        }
        if (title != null) {
            meta.setTitle(title);
        }
        if (author != null) {
            meta.setAuthor(author);
        }
        if (pages != null) {
            meta.setPages(pages);
        }
        return meta;
    }

    public static Map<String, Object> serializeBookMeta(BookMeta meta) {
        Map<String, Object> root = new HashMap<>();
        if (meta.hasTitle()) {
            root.put("title", meta.getTitle());
        }
        if (meta.hasAuthor()) {
            root.put("author", meta.getAuthor());
        }
        if (meta.hasPages()) {
            JsonArray jsonArray = new JsonArray();
            for (String page : meta.getPages()) {
                jsonArray.add(new JsonPrimitive(page));
            }
            root.put("pages", jsonArray);
        }

        return root;
    }

    public static EnchantmentStorageMeta getEnchantedBookMeta(Map<String, Object> data) {
        ItemStack dummyItems = new ItemStack(Material.ENCHANTED_BOOK, 1);

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) dummyItems.getItemMeta();
        if (data.containsKey("enchantments")) {
            Map<Enchantment, Integer> enchants = getEnchantments((String) data.get("enchantments"));
            for (Enchantment e : enchants.keySet()) {
                meta.addStoredEnchant(e, enchants.get(e), true);
            }
        }
        return meta;
    }

    public static Map<String, Object> serializeEnchantedBookMeta(EnchantmentStorageMeta meta) {
        Map<String, Object> root = new HashMap<>();

        String enchants = serializeEnchantments(meta.getStoredEnchants());
        root.put("enchantments", enchants);

        return root;
    }

    public static Map<String, Object> serializeArmor(LeatherArmorMeta meta) {
        Map<String, Object> root = new HashMap<>();
        root.put("color", serializeColor(meta.getColor()));
        return root;
    }

    @SuppressWarnings("unchecked")
    public static LeatherArmorMeta getLeatherArmorMeta(Map<String, Object> data) {
        ItemStack dummyItems = new ItemStack(Material.LEATHER_HELMET, 1);

        LeatherArmorMeta meta = (LeatherArmorMeta) dummyItems.getItemMeta();
        if (data.containsKey("color")) {
            meta.setColor(getColor((Map<String, Object>) data.get("color")));
        }

        return meta;
    }

    public static String serializeEnchantments(Map<Enchantment, Integer> enchantments) {
        StringBuilder serialized = new StringBuilder();
        for (Enchantment e : enchantments.keySet()) {
            serialized.append(e.getName()).append(":").append(enchantments.get(e)).append(";");
        }

        if (serialized.toString().length() == 0) {
            serialized.append("EMPTY");
        }

        return serialized.toString();
    }

    public static Map<Enchantment, Integer> getEnchantments(String serializedEnchants) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        if (serializedEnchants.isEmpty() || serializedEnchants.equalsIgnoreCase("EMPTY")) {
            return enchantments;
        }

        String[] enchants = serializedEnchants.split(";");
        for (String enchant : enchants) {
            String[] ench = enchant.split(":");
            enchantments.put(Enchantment.getByName(ench[0]), Integer.parseInt(ench[1]));
        }

        return enchantments;
    }

    @SuppressWarnings("unchecked")
    public static FireworkMeta getFireworkMeta(Map<String, Object> data) {
        FireworkMeta dummy = (FireworkMeta) new ItemStack(Material.FIREWORK).getItemMeta();
        dummy.setPower((Integer) data.get("power"));

        List<Map<String, Object>> effects = (List<Map<String, Object>>) data.get("effects");
        for (Map<String, Object> map : effects) {
            dummy.addEffect(getFireworkEffect(map));
        }

        return dummy;
    }

    public static Map<String, Object> serializeFireworkMeta(FireworkMeta meta) {
        Map<String, Object> root = new HashMap<>();
        root.put("power", meta.getPower());

        List<Object> effects = new ArrayList<>();
        for (FireworkEffect e : meta.getEffects()) {
            effects.add(serializeFireworkEffect(e));
        }
        root.put("effects", effects);

        return root;
    }

    @SuppressWarnings("unchecked")
    private static FireworkEffect getFireworkEffect(Map<String, Object> data) {
        FireworkEffect.Builder builder = FireworkEffect.builder();

        List<Map<String, Object>> colors = (List<Map<String, Object>>) data.get("colors");
        for (Map<String, Object> map : colors) {
            builder.withColor(getColor(map));
        }

        List<Map<String, Object>> fadeColors = (List<Map<String, Object>>) data.get("colors");
        for (Map<String, Object> map : fadeColors) {
            builder.withFade(getColor(map));
        }

        if ((Boolean) data.get("flicker")) {
            builder.withFlicker();
        }
        if ((Boolean) data.get("trail")) {
            builder.withTrail();
        }

        builder.with(FireworkEffect.Type.valueOf((String) data.get("type")));
        return builder.build();
    }

    private static Map<String, Object> serializeFireworkEffect(FireworkEffect effect) {
        Map<String, Object> root = new HashMap<>();

        List<Object> colors = new ArrayList<>();
        for (Color c : effect.getColors()) {
            colors.add(serializeColor(c));
        }
        root.put("colors", colors);

        List<Object> fadeColors = new ArrayList<>();
        for (Color c : effect.getFadeColors()) {
            fadeColors.add(serializeColor(c));
        }
        root.put("fade-colors", fadeColors);

        root.put("flicker", effect.hasFlicker());
        root.put("trail", effect.hasTrail());
        root.put("type", effect.getType().name());

        return root;
    }


    private static Color getColor(Map<String, Object> data) {
        int r = 0, g = 0, b = 0;
        if (data.containsKey("red")) {
            r = (Integer) data.get("red");
        }
        if (data.containsKey("green")) {
            g = (Integer) data.get("green");
        }
        if (data.containsKey("blue")) {
            b = (Integer) data.get("blue");
        }
        return Color.fromRGB(r, g, b);
    }

    private static Map<String, Integer> serializeColor(Color color) {
        Map<String, Integer> root = new HashMap<>();
        root.put("red", color.getRed());
        root.put("green", color.getGreen());
        root.put("blue", color.getBlue());
        return root;
    }

    public static boolean isLeatherArmor(Material material) {
        return material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS;
    }

    public static Map<String, Object> serializeSkull(SkullMeta meta) {
        Map<String, Object> root = new HashMap<>();
        if (meta.hasOwner()) {
            root.put("owner", meta.getOwner());
        }
        return root;
    }

    public static SkullMeta getSkullMeta(Map<String, Object> data) {
        ItemStack dummyItems = new ItemStack(Material.SKULL_ITEM);
        SkullMeta dummyMeta = (SkullMeta) dummyItems.getItemMeta();
        if (data.containsKey("owner")) {
            dummyMeta.setOwner((String) data.get("owner"));
        }
        return dummyMeta;
    }
}
