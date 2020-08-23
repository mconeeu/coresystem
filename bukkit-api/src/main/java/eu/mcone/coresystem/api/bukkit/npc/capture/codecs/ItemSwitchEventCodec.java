/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.ItemStackTypeAdapterUtils;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class ItemSwitchEventCodec extends Codec<PlayerItemHeldEvent, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private int material;
    private String enchantments = "";

    public ItemSwitchEventCodec() {
        super((byte) 4, (byte) 2);
    }

    @Override
    public Object[] decode(Player player, PlayerItemHeldEvent event) {
        ItemStack previousItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (previousItem == null && newItem == null) {
            return null;
        } else if (previousItem != null && newItem != null) {
            if (previousItem.getType() == newItem.getType()) {
                return null;
            }
        }

        this.material = (newItem != null ? newItem.getType() : Material.AIR).getId();
        this.enchantments = (newItem != null ? ItemStackTypeAdapterUtils.serializeEnchantments(newItem.getEnchantments()) : "");
        return new Object[]{event.getPlayer()};
    }

    @Override
    public void encode(PlayerNpc npc) {
        npc.setItemInHand(getItem());
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(material);
        out.writeUTF(enchantments);
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        material = in.readInt();
        enchantments = in.readUTF();
    }

    public ItemStack getItem() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.getMaterial(material), 1);
        if (enchantments.isEmpty()) {
            itemBuilder.enchantments(ItemStackTypeAdapterUtils.getEnchantments(enchantments));
        }
        return itemBuilder.create();
    }

    @Override
    public String toString() {
        return "ItemSwitchEventCodec{" +
                "material=" + material +
                ", enchantments='" + enchantments + '\'' +
                '}';
    }
}
