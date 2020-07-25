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

    private short material;
    private String enchantments;

    public ItemSwitchEventCodec(eu.mcone.coresystem.api.bukkit.npc.capture.codecs.ItemSwitchEventCodec old) {
        super((byte) 2, (byte) 3);
        this.material = (short) Material.getMaterial(old.getMaterial()).getId();
        this.enchantments = old.getEnchantments();
    }

    public ItemSwitchEventCodec() {
        super((byte) 2, (byte) 2);
    }

    @Override
    public Object[] decode(Player player, PlayerItemHeldEvent event) {
        ItemStack previousItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (previousItem != null && newItem != null) {
            if (previousItem.getType() != newItem.getType()) {
                ItemStack itemStack = event.getPlayer().getItemInHand();
                this.material = (short) itemStack.getType().getId();
                this.enchantments = ItemStackTypeAdapterUtils.serializeEnchantments(itemStack.getEnchantments());
                return new Object[]{event.getPlayer()};
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void encode(PlayerNpc npc) {
        npc.setItemInHand(getItem());
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeShort(material);
        out.writeUTF(enchantments);
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        material = in.readShort();
        enchantments = in.readUTF();
    }

    public ItemStack getItem() {
        return new ItemBuilder(Material.getMaterial(material), 1).enchantments(ItemStackTypeAdapterUtils.getEnchantments(enchantments)).create();
    }

    @Override
    public String toString() {
        return "ItemSwitchEventCodec{" +
                "material=" + material +
                ", enchantments='" + enchantments + '\'' +
                '}';
    }
}
