/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config.typeadapter.gson;

import com.google.gson.*;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.ItemStackTypeAdapterUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftItemStackTypeAdapter implements JsonSerializer<CraftItemStack>, JsonDeserializer<CraftItemStack> {

    @Override
    public CraftItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Material material = Material.getMaterial(jsonObject.get("material").getAsString());
        String name = "";
        Map<Enchantment, Integer> enchants = null;
        List<String> lore = new ArrayList<>();
        int repairPenalty = 0;

        if (jsonObject.has("name")) {
            name = jsonObject.get("name").getAsString();
        }
        if (jsonObject.has("enchantments")) {
            enchants = ItemStackTypeAdapterUtils.getEnchantments(jsonObject.get("enchantments").getAsString());
        }
        if (jsonObject.has("lore")) {
            JsonArray array = jsonObject.getAsJsonArray("lore");
            for (int j = 0; j < array.size(); j++) {
                lore.add(array.get(j).getAsString());
            }
        }
        if (jsonObject.has("repairPenalty")) {
            repairPenalty = jsonObject.get("repairPenalty").getAsInt();
        }

        ItemStack stuff = new ItemStack(material, jsonObject.get("amount").getAsInt(), (short) jsonObject.get("durability").getAsInt());
        if ((material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK) && jsonObject.has("book-meta")) {
            BookMeta meta = ItemStackTypeAdapterUtils.getBookMeta(context.deserialize(jsonObject.get("book-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.ENCHANTED_BOOK && jsonObject.has("book-meta")) {
            EnchantmentStorageMeta meta = ItemStackTypeAdapterUtils.getEnchantedBookMeta(context.deserialize(jsonObject.get("book-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (ItemStackTypeAdapterUtils.isLeatherArmor(material) && jsonObject.has("armor-meta")) {
            LeatherArmorMeta meta = ItemStackTypeAdapterUtils.getLeatherArmorMeta(context.deserialize(jsonObject.get("armor-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.SKULL_ITEM && jsonObject.has("skull-meta")) {
            SkullMeta meta = ItemStackTypeAdapterUtils.getSkullMeta(context.deserialize(jsonObject.get("skull-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.FIREWORK && jsonObject.has("firework-meta")) {
            FireworkMeta meta = ItemStackTypeAdapterUtils.getFireworkMeta(context.deserialize(jsonObject.get("firework-meta"), Map.class));
            stuff.setItemMeta(meta);
        }

        ItemMeta meta = stuff.getItemMeta();
        if (name != null) {
            meta.setDisplayName(name);
        }
        meta.setLore(lore);
        stuff.setItemMeta(meta);

        if (repairPenalty != 0) {
            Repairable rep = (Repairable) meta;
            rep.setRepairCost(repairPenalty);
            stuff.setItemMeta((ItemMeta) rep);
        }

        if (enchants != null) {
            stuff.addUnsafeEnchantments(enchants);
        }

        return CraftItemStack.asCraftCopy(stuff);
    }

    @Override
    public JsonElement serialize(CraftItemStack itemStack, Type type, JsonSerializationContext context) {
        Gson gson = CoreSystem.getInstance().getGson();
        JsonObject values = new JsonObject();
        if (itemStack == null) {
            return null;
        }

        boolean hasMeta = itemStack.hasItemMeta();
        String name = null, enchants = null;
        String[] lore = null;
        int repairPenalty = 0;
        Material material = itemStack.getType();
        Map<String, Object> bookMeta = null, armorMeta = null, skullMeta = null, fwMeta = null;

        if (material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK) {
            bookMeta = ItemStackTypeAdapterUtils.serializeBookMeta((BookMeta) itemStack.getItemMeta());
        } else if (material == Material.ENCHANTED_BOOK) {
            bookMeta = ItemStackTypeAdapterUtils.serializeEnchantedBookMeta((EnchantmentStorageMeta) itemStack.getItemMeta());
        } else if (ItemStackTypeAdapterUtils.isLeatherArmor(material)) {
            armorMeta = ItemStackTypeAdapterUtils.serializeArmor((LeatherArmorMeta) itemStack.getItemMeta());
        } else if (material == Material.SKULL_ITEM) {
            skullMeta = ItemStackTypeAdapterUtils.serializeSkull((SkullMeta) itemStack.getItemMeta());
        } else if (material == Material.FIREWORK) {
            fwMeta = ItemStackTypeAdapterUtils.serializeFireworkMeta((FireworkMeta) itemStack.getItemMeta());
        }

        if (hasMeta) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }
            if (meta.hasLore()) {
                lore = meta.getLore().toArray(new String[]{});
            }
            if (meta.hasEnchants())
                enchants = ItemStackTypeAdapterUtils.serializeEnchantments(meta.getEnchants());
            if (meta instanceof Repairable) {
                Repairable rep = (Repairable) meta;
                if (rep.hasRepairCost()) {
                    repairPenalty = rep.getRepairCost();
                }
            }
        }

        values.addProperty("material", itemStack.getType().name());
        values.addProperty("durability", itemStack.getDurability());
        values.addProperty("amount", itemStack.getAmount());
        if (name != null) {
            values.addProperty("name", name);
        }
        if (enchants != null) {
            values.addProperty("enchantments", enchants);
        }
        if (lore != null) {
            JsonArray jsonArray = new JsonArray();
            for (String loreElement : lore) {
                jsonArray.add(new JsonPrimitive(loreElement));
            }
            values.add("lore", jsonArray);
        }
        if (repairPenalty != 0) {
            values.addProperty("repairPenalty", repairPenalty);
        }
        if (bookMeta != null && bookMeta.size() > 0) {
            values.add("book-meta", gson.toJsonTree(bookMeta));
        }
        if (armorMeta != null && armorMeta.size() > 0) {
            values.add("armor-meta", gson.toJsonTree(armorMeta));
        }
        if (skullMeta != null && skullMeta.size() > 0) {
            values.add("skull-meta", gson.toJsonTree(skullMeta));
        }
        if (fwMeta != null && fwMeta.size() > 0) {
            values.add("firework-meta", gson.toJsonTree(fwMeta));
        }

        return values;
    }

}
