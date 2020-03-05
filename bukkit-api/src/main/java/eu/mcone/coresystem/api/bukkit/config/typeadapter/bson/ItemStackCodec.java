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
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class ItemStackCodec implements Codec<ItemStack> {

    private final CodecRegistry registry;

    public ItemStackCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public ItemStack decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = registry.get(Document.class).decode(reader, decoderContext);

        Material material = Material.valueOf(document.getString("material"));
        String name = null;
        Map<Enchantment, Integer> enchants = null;
        List<String> lore = new ArrayList<>();
        Set<ItemFlag> itemFlags = new HashSet<>();
        short durability = document.getInteger("durability").shortValue();

        if (document.containsKey("name")) {
            name = document.getString("name");
        }
        if (document.containsKey("enchantments")) {
            enchants = ItemStackTypeAdapterUtils.getEnchantments(document.getString("enchantments"));
        }
        if (document.containsKey("lore")) {
            lore = document.getList("lore", String.class);
        }
        if (document.containsKey("itemFlags")) {
            for (String flag : document.getList("itemFlags", String.class)) {
                itemFlags.add(ItemFlag.valueOf(flag));
            }
        }

        ItemStack stuff = new ItemStack(material, document.getInteger("amount"));
        if ((material == Material.WRITABLE_BOOK || material == Material.WRITTEN_BOOK) && document.containsKey("book-meta")) {
            BookMeta meta = ItemStackTypeAdapterUtils.getBookMeta(document.get("book-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.ENCHANTED_BOOK && document.containsKey("book-meta")) {
            EnchantmentStorageMeta meta = ItemStackTypeAdapterUtils.getEnchantedBookMeta(document.get("book-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (ItemStackTypeAdapterUtils.isLeatherArmor(material) && document.containsKey("armor-meta")) {
            LeatherArmorMeta meta = ItemStackTypeAdapterUtils.getLeatherArmorMeta(document.get("armor-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if ((material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD) && document.containsKey("skull-meta")) {
            SkullMeta meta = ItemStackTypeAdapterUtils.getSkullMeta(document.get("skull-meta", Document.class));
            stuff.setItemMeta(meta);
        } else if (material == Material.FIREWORK_ROCKET && document.containsKey("firework-meta")) {
            FireworkMeta meta = ItemStackTypeAdapterUtils.getFireworkMeta(document.get("firework-meta", Document.class));
            stuff.setItemMeta(meta);
        }

        ItemMeta meta = stuff.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }
            meta.setLore(lore);
            if (document.containsKey("unbreakable")) {
                meta.setUnbreakable(document.getBoolean("unbreakable"));
            }
            if (itemFlags.size() > 0) {
                meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            }
            if (durability != 0) {
                Repairable rep = (Repairable) meta;
                rep.setRepairCost(durability);
            }

            stuff.setItemMeta(meta);
        }

        if (enchants != null) {
            stuff.addUnsafeEnchantments(enchants);
        }

        return CraftItemStack.asCraftCopy(stuff);
    }

    @Override
    public void encode(BsonWriter writer, ItemStack itemStack, EncoderContext encoderContext) {
        Document document = new Document();
        if (itemStack == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        boolean unbreakable = false;
        String name = null, enchants = null;
        List<String> lore = null;
        Set<ItemFlag> itemFlags = null;
        int repairPenalty = 0;
        Material material = itemStack.getType();
        Map<String, Object> bookMeta = null, armorMeta = null, skullMeta = null, fwMeta = null;

        if (meta != null) {
            if (material == Material.WRITABLE_BOOK || material == Material.WRITTEN_BOOK) {
                bookMeta = ItemStackTypeAdapterUtils.serializeBookMeta((BookMeta) meta);
            } else if (material == Material.ENCHANTED_BOOK) {
                bookMeta = ItemStackTypeAdapterUtils.serializeEnchantedBookMeta((EnchantmentStorageMeta) meta);
            } else if (ItemStackTypeAdapterUtils.isLeatherArmor(material)) {
                armorMeta = ItemStackTypeAdapterUtils.serializeArmor((LeatherArmorMeta) meta);
            } else if (material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD) {
                skullMeta = ItemStackTypeAdapterUtils.serializeSkull((SkullMeta) meta);
            } else if (material == Material.FIREWORK_ROCKET) {
                fwMeta = ItemStackTypeAdapterUtils.serializeFireworkMeta((FireworkMeta) meta);
            }

            if (meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            unbreakable = meta.isUnbreakable();
            itemFlags = meta.getItemFlags();
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
        document.append("amount", itemStack.getAmount());
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
        if (unbreakable) {
            document.append("unbreakable", true);
        }
        if (itemFlags != null && itemFlags.size() > 0) {
            List<String> flags = new ArrayList<>();
            itemFlags.forEach(flag -> flags.add(flag.toString()));

            document.append("itemFlags", flags);
        }
        if (bookMeta != null && bookMeta.size() > 0) {
            document.append("book-meta", bookMeta);
        }
        if (armorMeta != null && armorMeta.size() > 0) {
            document.append("armor-meta", armorMeta);
        }
        if (skullMeta != null && skullMeta.size() > 0) {
            document.append("skull-meta", skullMeta);
        }
        if (fwMeta != null && fwMeta.size() > 0) {
            document.append("firework-meta", fwMeta);
        }

        registry.get(Document.class).encode(writer, document, encoderContext);
    }

    @Override
    public Class<ItemStack> getEncoderClass() {
        return ItemStack.class;
    }

}
