/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.config.typeadapter.bson;

import eu.mcone.coresystem.api.bukkit.config.typeadapter.ItemStackTypeAdapterUtils;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftItemStackCodec implements Codec<CraftItemStack> {

    private final CodecRegistry registry;

    public CraftItemStackCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public CraftItemStack decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = registry.get(Document.class).decode(reader, decoderContext);

        Material material = Material.getMaterial(document.getString("material"));
        String name = null;
        Map<Enchantment, Integer> enchants = null;
        List<String> lore = new ArrayList<>();
        int repairPenalty = 0;

        if (document.containsKey("name")) {
            name = document.getString("name");
        }
        if (document.containsKey("enchantments")) {
            enchants = ItemStackTypeAdapterUtils.getEnchantments(document.getString("enchantments"));
        }
        if (document.containsKey("lore")) {
            lore = document.getList("lore", String.class);
        }
        if (document.containsKey("repairPenalty")) {
            repairPenalty = document.getInteger("repairPenalty");
        }

        ItemStack stuff = new ItemStack(material, 1, document.getInteger("durability").shortValue());
        if ((material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK) && document.containsKey("book-meta")) {
            BookMeta meta = ItemStackTypeAdapterUtils.getBookMeta(document.get("book-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.ENCHANTED_BOOK && document.containsKey("book-meta")) {
            EnchantmentStorageMeta meta = ItemStackTypeAdapterUtils.getEnchantedBookMeta(document.get("book-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (ItemStackTypeAdapterUtils.isLeatherArmor(material) && document.containsKey("armor-meta")) {
            LeatherArmorMeta meta = ItemStackTypeAdapterUtils.getLeatherArmorMeta(document.get("armor-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.SKULL_ITEM && document.containsKey("skull-meta")) {
            SkullMeta meta = ItemStackTypeAdapterUtils.getSkullMeta(document.get("skull-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.FIREWORK && document.containsKey("firework-meta")) {
            FireworkMeta meta = ItemStackTypeAdapterUtils.getFireworkMeta(document.get("firework-meta", Document.class));
            stuff.setItemMeta(meta);
        }

        ItemMeta meta = stuff.getItemMeta();

        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }
            meta.setLore(lore);
            stuff.setItemMeta(meta);
        }

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
    public void encode(BsonWriter writer, CraftItemStack itemStack, EncoderContext encoderContext) {
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

        registry.get(Document.class).encode(writer, document, encoderContext);
    }

    @Override
    public Class<CraftItemStack> getEncoderClass() {
        return CraftItemStack.class;
    }

}
