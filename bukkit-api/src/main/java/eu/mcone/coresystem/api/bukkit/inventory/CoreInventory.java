/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CoreInventory implements ItemEventStore {

    public static final String PLACEHOLDER_ITEM_DISPLAYNAME = "§8//§oMCONE§8//";
    public static final ItemStack PLACEHOLDER_ITEM = makePlaceholderItem(DyeColor.GRAY);

    public static final ItemStack BACK_ITEM = new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück").create();

    public static final ItemStack UP_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/a156b31cbf8f774547dc3f9713a770ecc5c727d967cb0093f26546b920457387").toItemBuilder().displayName("§f§lNach Oben").lore("", "§8» §f§nLinksklick§8 | §7§oInhalt laden").create();
    public static final ItemStack UP_BLOCKED_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/58fe251a40e4167d35d081c27869ac151af96b6bd16dd2834d5dc7235f47791d").toItemBuilder().displayName("§7§lErste Seite").create();
    public static final ItemStack DOWN_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/fe3d755cecbb13a39e8e9354823a9a02a01dce0aca68ffd42e3ea9a9d29e2df2").toItemBuilder().displayName("§f§lNach Unten").lore("", "§8» §f§nLinksklick§8 | §7§oInhalt laden").create();
    public static final ItemStack DOWN_BLOCKED_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/9b7ce683d0868aa4378aeb60caa5ea80596bcffdab6b5af2d12595837a84853").toItemBuilder().displayName("§7§lLetzte Seite").create();
    public static final ItemStack LEFT_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23").toItemBuilder().displayName("§7Vorherige Seite").lore("", "§8» §f§nLinksklick§8 | §7§oInhalt laden").create();
    public static final ItemStack LEFT_BLOCKED_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/bb0f6e8af46ac6faf88914191ab66f261d6726a7999c637cf2e4159fe1fc477").toItemBuilder().displayName("§7§lErste Seite").create();
    public static final ItemStack RIGHT_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b").toItemBuilder().displayName("§7Nächste Seite").lore("", "§8» §f§nLinksklick§8 | §7§oInhalt laden").create();
    public static final ItemStack RIGHT_BLOCKED_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/f2f3a2dfce0c3dab7ee10db385e5229f1a39534a8ba2646178e37c4fa93b").toItemBuilder().displayName("§7§lLetzte Seite").create();
    public static final ItemStack REFRESH_ITEM = Skull.fromUrl("http://textures.minecraft.net/texture/e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61").toItemBuilder().displayName("§7Neu laden").lore("", "§8» §f§nLinksklick§8 | §7§oNeu laden").create();

    @Getter
    protected final Player player;
    @Getter
    protected final Inventory inventory;
    @Getter
    protected Map<Integer, CoreItemStack> items;
    @Getter
    private final boolean allowModification;

    /**
     * creates new CoreInventory
     *
     * @param title   inventory title
     * @param size    inventory size
     * @param options options
     */
    public CoreInventory(String title, Player player, int size, InventoryOption... options) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.items = new HashMap<>();
        this.allowModification = Arrays.asList(options).contains(InventoryOption.ALLOW_MODIFICATION);

        if (Arrays.asList(options).contains(InventoryOption.FILL_EMPTY_SLOTS)) {
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, PLACEHOLDER_ITEM);
            }
        }
    }

    /**
     * sets an item in the inventory
     *
     * @param slot  inventory slot
     * @param item  item stack
     * @param event event, called when player clicks on the item
     */
    public void setItem(int slot, ItemStack item, CoreItemEvent event) {
        items.put(slot, new CoreItemStack(item, event));
        inventory.setItem(slot, item);
    }

    /**
     * sets an item in the inventory
     *
     * @param slot inventory slot
     * @param item item stack
     */
    public void setItem(int slot, ItemStack item) {
        items.put(slot, new CoreItemStack(item, null));
        inventory.setItem(slot, item);
    }

    /**
     * opens the inventory
     */
    public Inventory openInventory() {
        CoreSystem.getInstance().getPluginManager().registerCoreInventory(player, this);

        player.openInventory(inventory);
        return inventory;
    }

    public static ItemStack makePlaceholderItem(DyeColor color) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE, 1, color.getWoolData()).displayName(PLACEHOLDER_ITEM_DISPLAYNAME).create();
    }

}
