/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.json;

import com.google.gson.*;
import com.mongodb.MongoClientSettings;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackTypeAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>, Codec<ItemStack> {

    private static final Codec<Document> documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);

    @SuppressWarnings("Duplicates")
    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
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
            enchants = getEnchantments(jsonObject.get("enchantments").getAsString());
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

        ItemStack stuff = new ItemStack(material, 1, (short) jsonObject.get("durability").getAsInt());
        if ((material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK) && jsonObject.has("book-meta")) {
            BookMeta meta = getBookMeta(context.deserialize(jsonObject.get("book-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.ENCHANTED_BOOK && jsonObject.has("book-meta")) {
            EnchantmentStorageMeta meta = getEnchantedBookMeta(context.deserialize(jsonObject.get("book-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (isLeatherArmor(material) && jsonObject.has("armor-meta")) {
            LeatherArmorMeta meta = getLeatherArmorMeta(context.deserialize(jsonObject.get("armor-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.SKULL_ITEM && jsonObject.has("skull-meta")) {
            SkullMeta meta = getSkullMeta(context.deserialize(jsonObject.get("skull-meta"), Map.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.FIREWORK && jsonObject.has("firework-meta")) {
            FireworkMeta meta = getFireworkMeta(context.deserialize(jsonObject.get("firework-meta"), Map.class));
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

        return stuff;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        Gson gson = BukkitCoreSystem.getSystem().getGson();
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
            bookMeta = serializeBookMeta((BookMeta) itemStack.getItemMeta());
        } else if (material == Material.ENCHANTED_BOOK) {
            bookMeta = serializeEnchantedBookMeta((EnchantmentStorageMeta) itemStack.getItemMeta());
        } else if (isLeatherArmor(material)) {
            armorMeta = serializeArmor((LeatherArmorMeta) itemStack.getItemMeta());
        } else if (material == Material.SKULL_ITEM) {
            skullMeta = serializeSkull((SkullMeta) itemStack.getItemMeta());
        } else if (material == Material.FIREWORK) {
            fwMeta = serializeFireworkMeta((FireworkMeta) itemStack.getItemMeta());
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
                enchants = serializeEnchantments(meta.getEnchants());
            if (meta instanceof Repairable) {
                Repairable rep = (Repairable) meta;
                if (rep.hasRepairCost()) {
                    repairPenalty = rep.getRepairCost();
                }
            }
        }

        values.addProperty("material", itemStack.getType().name());
        values.addProperty("durability", itemStack.getDurability());
        if (name != null) {
            values.addProperty("name", name);
        }
        if (enchants != null) {
            values.addProperty("enchantments", enchants);
        }
        if (lore != null) {
            JsonArray jsonArray = new JsonArray();
            for (String loreElement : lore) {
                jsonArray.add(loreElement);
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

    @SuppressWarnings("Duplicates")
    @Override
    public ItemStack decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);

        Material material = Material.getMaterial(document.getString("material"));
        String name = "";
        Map<Enchantment, Integer> enchants = null;
        List<String> lore = new ArrayList<>();
        int repairPenalty = 0;

        if (document.containsKey("name")) {
            name = document.getString("name");
        }
        if (document.containsKey("enchantments")) {
            enchants = getEnchantments(document.getString("enchantments"));
        }
        if (document.containsKey("lore")) {
            lore = document.getList("lore", String.class);
        }
        if (document.containsKey("repairPenalty")) {
            repairPenalty = document.getInteger("repairPenalty");
        }

        ItemStack stuff = new ItemStack(material, 1, document.getInteger("durability").shortValue());
        if ((material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK) && document.containsKey("book-meta")) {
            BookMeta meta = getBookMeta(document.get("book-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.ENCHANTED_BOOK && document.containsKey("book-meta")) {
            EnchantmentStorageMeta meta = getEnchantedBookMeta(document.get("book-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (isLeatherArmor(material) && document.containsKey("armor-meta")) {
            LeatherArmorMeta meta = getLeatherArmorMeta(document.get("armor-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.SKULL_ITEM && document.containsKey("skull-meta")) {
            SkullMeta meta = getSkullMeta(document.get("skull-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.FIREWORK && document.containsKey("firework-meta")) {
            FireworkMeta meta = getFireworkMeta(document.get("firework-meta", Document.class));
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

        return stuff;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void encode(BsonWriter writer, ItemStack itemStack, EncoderContext encoderContext) {
        Document document = new Document();
        if (itemStack == null) {
            return;
        }

        boolean hasMeta = itemStack.hasItemMeta();
        String name = null, enchants = null;
        String[] lore = null;
        int repairPenalty = 0;
        Material material = itemStack.getType();
        Map<String, Object> bookMeta = null, armorMeta = null, skullMeta = null, fwMeta = null;

        if (material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK) {
            bookMeta = serializeBookMeta((BookMeta) itemStack.getItemMeta());
        } else if (material == Material.ENCHANTED_BOOK) {
            bookMeta = serializeEnchantedBookMeta((EnchantmentStorageMeta) itemStack.getItemMeta());
        } else if (isLeatherArmor(material)) {
            armorMeta = serializeArmor((LeatherArmorMeta) itemStack.getItemMeta());
        } else if (material == Material.SKULL_ITEM) {
            skullMeta = serializeSkull((SkullMeta) itemStack.getItemMeta());
        } else if (material == Material.FIREWORK) {
            fwMeta = serializeFireworkMeta((FireworkMeta) itemStack.getItemMeta());
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
                enchants = serializeEnchantments(meta.getEnchants());
            if (meta instanceof Repairable) {
                Repairable rep = (Repairable) meta;
                if (rep.hasRepairCost()) {
                    repairPenalty = rep.getRepairCost();
                }
            }
        }

        document.append("material", itemStack.getType().name());
        document.append("durability", itemStack.getDurability());
        if (name != null) {
            document.append("name", name);
        }
        if (enchants != null) {
            document.append("enchantments", enchants);
        }
        if (lore != null) {
            document.append("lore", lore);
        }
        if (repairPenalty != 0) {
            document.append("repairPenalty", repairPenalty);
        }
        if (bookMeta != null && bookMeta.size() > 0) {
            document.append("book-meta", Document.parse(bookMeta.toString()));
        }
        if (armorMeta != null && armorMeta.size() > 0) {
            document.append("armor-meta", Document.parse(armorMeta.toString()));
        }
        if (skullMeta != null && skullMeta.size() > 0) {
            document.append("skull-meta", Document.parse(skullMeta.toString()));
        }
        if (fwMeta != null && fwMeta.size() > 0) {
            document.append("firework-meta", Document.parse(fwMeta.toString()));
        }

        documentCodec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<ItemStack> getEncoderClass() {
        return ItemStack.class;
    }



    @SuppressWarnings("unchecked")
    private BookMeta getBookMeta(Map<String, Object> data) {
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

    private Map<String, Object> serializeBookMeta(BookMeta meta) {
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
                jsonArray.add(page);
            }
            root.put("pages", jsonArray);
        }

        return root;
    }

    private EnchantmentStorageMeta getEnchantedBookMeta(Map<String, Object> data) {
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

    private Map<String, Object> serializeEnchantedBookMeta(EnchantmentStorageMeta meta) {
        Map<String, Object> root = new HashMap<>();

        String enchants = serializeEnchantments(meta.getStoredEnchants());
        root.put("enchantments", enchants);

        return root;
    }

    private Map<String, Object> serializeArmor(LeatherArmorMeta meta) {
        Map<String, Object> root = new HashMap<>();
        root.put("color", serializeColor(meta.getColor()));
        return root;
    }

    @SuppressWarnings("unchecked")
    private LeatherArmorMeta getLeatherArmorMeta(Map<String, Object> data) {
        ItemStack dummyItems = new ItemStack(Material.LEATHER_HELMET, 1);

        LeatherArmorMeta meta = (LeatherArmorMeta) dummyItems.getItemMeta();
        if (data.containsKey("color")) {
            meta.setColor(getColor((Map<String, Object>) data.get("color")));
        }

        return meta;
    }

    private String serializeEnchantments(Map<Enchantment, Integer> enchantments) {
        StringBuilder serialized = new StringBuilder();
        for (Enchantment e : enchantments.keySet()) {
            serialized.append(e.getName()).append(":").append(enchantments.get(e)).append(";");
        }

        return serialized.toString();
    }

    private Map<Enchantment, Integer> getEnchantments(String serializedEnchants) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        if (serializedEnchants.isEmpty()) {
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
    private FireworkMeta getFireworkMeta(Map<String, Object> data) {
        FireworkMeta dummy = (FireworkMeta) new ItemStack(Material.FIREWORK).getItemMeta();
        dummy.setPower((Integer) data.get("power"));

        List<Map<String, Object>> effects = (List<Map<String, Object>>) data.get("effects");
        for (Map<String, Object> map : effects) {
            dummy.addEffect(getFireworkEffect(map));
        }

        return dummy;
    }

    private Map<String, Object> serializeFireworkMeta(FireworkMeta meta) {
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
    private FireworkEffect getFireworkEffect(Map<String, Object> data) {
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

    private Map<String, Object> serializeFireworkEffect(FireworkEffect effect) {
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


    private Color getColor(Map<String, Object> data) {
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

    private Map<String, Integer> serializeColor(Color color) {
        Map<String, Integer> root = new HashMap<>();
        root.put("red", color.getRed());
        root.put("green", color.getGreen());
        root.put("blue", color.getBlue());
        return root;
    }

    private boolean isLeatherArmor(Material material) {
        return material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS;
    }

    private Map<String, Object> serializeSkull(SkullMeta meta) {
        Map<String, Object> root = new HashMap<>();
        if (meta.hasOwner()) {
            root.put("owner", meta.getOwner());
        }
        return root;
    }

    private SkullMeta getSkullMeta(Map<String, Object> data) {
        ItemStack dummyItems = new ItemStack(Material.SKULL_ITEM);
        SkullMeta dummyMeta = (SkullMeta) dummyItems.getItemMeta();
        if (data.containsKey("owner")) {
            dummyMeta.setOwner((String) data.get("owner"));
        }
        return dummyMeta;
    }

}
