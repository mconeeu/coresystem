/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class Skull extends ExtendedItemBuilder<SkullMeta> {

    private List<String> lore;

    /**
     * create ItemBuilder for SkullItem by owner
     *
     * @param owner owner of the skull
     */
    public Skull(String owner) {
        this(owner, 1);
    }

    /**
     * create ItemBuilder for SkullItem by owner
     *
     * @param owner  owner of the skull
     * @param amount amount of items in ItemStack
     */
    public Skull(String owner, int amount) {
        this(amount);
        meta.setOwner(owner);
    }

    /**
     * change displayname of the item
     *
     * @param displayName displayname
     * @return this
     */
    public Skull setDisplayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    /**
     * set loren of the item
     *
     * @param lore loren (Array)
     * @return this
     */
    public Skull lore(String... lore) {
        this.lore = new ArrayList<>(Arrays.asList(lore));
        return this;
    }

    /**
     * set loren of the item
     *
     * @param loren loren (ArrayList)
     * @return this
     */
    public Skull lore(List<String> loren) {
        this.lore = loren;
        return this;
    }

    /**
     * add a lore of the item
     *
     * @param lore lore (String)
     * @return this
     */
    public Skull addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    @Override
    public ItemStack getItemStack() {
        if (lore != null && !lore.isEmpty())
            meta.setLore(this.lore);
        return super.getItemStack();
    }

    /**
     * create ItemBuilder for SkullItem by predefined Skin
     *
     * @param skin   BCS SkinInfo object
     * @param amount amount of items in ItemStack
     */
    public Skull(SkinInfo skin, int amount) {
        this(amount, new Property("textures", skin.getValue(), skin.getSignature()));
    }

    private Skull(int amount, Property texture) {
        this(amount);

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", texture);

        Field profileField;
        try {
            profileField = ((SkullMeta) meta).getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Skull(int amount) {
        super(new ItemStack(Material.SKULL_ITEM, amount, (short) SkullType.PLAYER.ordinal()));
    }

    private Skull(ItemStack item) {
        super(item);
    }

    /**
     * create ItemBuilder for SkullItem by URL
     *
     * @param url    url of skin.png
     * @param amount amount of items in ItemStack
     * @return new ItemBuilder
     */
    public static Skull fromUrl(String url, int amount) {
        return new Skull(amount, new Property(
                "textures",
                new String(Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()))
        ));
    }

    /**
     * wraps an existing ItemStack which must be of Material.SKULL in an Skull object
     *
     * @param skull ItemStack
     * @return new Skull instance
     * @throws ClassCastException if ItemStack has a conflicting Material
     */
    public static Skull wrap(ItemStack skull) throws ClassCastException {
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        return new Skull(skull);
    }

    /**
     * create ItemBuilder for SkullItem by URL
     *
     * @param url url of skin.png
     * @return new ItemBuilder
     */
    public static Skull fromUrl(String url) {
        return fromUrl(url, 1);
    }

    public Skull setOwner(String owner) {
        meta.setOwner(owner);
        return this;
    }

    /**
     * Gets the owner of the skull.
     *
     * @return the owner of the ckull
     */
    public String getOwner() {
        return meta.getOwner();
    }

    /**
     * Checks to see if the skull has an owner.
     *
     * @return true if the skull has an owner
     */
    public boolean hasOwner() {
        return meta.hasOwner();
    }

    public Skull setPlayer(Player player) {
        player.getInventory().setHelmet(getItemStack());
        return this;
    }

}
