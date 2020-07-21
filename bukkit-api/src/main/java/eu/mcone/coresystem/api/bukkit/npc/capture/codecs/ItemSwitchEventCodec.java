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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Getter
public class ItemSwitchEventCodec extends Codec<PlayerItemHeldEvent, PlayerNpc> {

    private String material;
    private int amount;
    private String enchantments;

    public ItemSwitchEventCodec() {
        super("SWITCH_ITEM", PlayerItemHeldEvent.class, PlayerNpc.class);
    }

    @Override
    public Object[] decode(Player player, PlayerItemHeldEvent event) {
        ItemStack previousItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (previousItem != null && newItem != null) {
            ItemStack itemStack = event.getPlayer().getItemInHand();
            this.material = itemStack.getType().toString();
            this.amount = itemStack.getAmount();
            this.enchantments = ItemStackTypeAdapterUtils.serializeEnchantments(itemStack.getEnchantments());
            return new Object[]{event.getPlayer()};
        } else {
            return null;
        }
    }

    @Override
    public void encode(PlayerNpc npc) {
        npc.setItemInHand(getItem());
    }

    @Override
    public void onWriteObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(material);
        out.writeInt(amount);
        out.writeUTF(enchantments);
    }

    @Override
    public void onReadObject(ObjectInputStream in) throws IOException {
        material = in.readUTF();
        amount = in.readInt();
        enchantments = in.readUTF();
    }

    public ItemStack getItem() {
        return new ItemBuilder(Material.valueOf(material), amount).enchantments(ItemStackTypeAdapterUtils.getEnchantments(enchantments)).create();
    }
}
