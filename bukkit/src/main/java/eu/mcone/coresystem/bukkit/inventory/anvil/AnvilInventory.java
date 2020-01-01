/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.anvil;

import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEventHandler;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilSlot;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.CoreAnvilInventory;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.Getter;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.Containers;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AnvilInventory implements CoreAnvilInventory {

    @Getter
    private final String title;
    @Getter
    private final AnvilClickEventHandler handler;
    private final Map<AnvilSlot, ItemStack> items;
    private final Map<Player, Inventory> inventories;

    public AnvilInventory(String title, AnvilClickEventHandler handler) {
        this.title = title;
        this.handler = handler;
        this.items = new HashMap<>();
        this.inventories = new HashMap<>();

        BukkitCoreSystem.getSystem().getPluginManager().registerCoreAnvilInventory(this);
    }

    @Override
    public AnvilInventory setItem(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    @Override
    public Inventory open(Player player) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        AnvilContainer container = new AnvilContainer(player, title);
        Inventory inventory = container.getBukkitView().getTopInventory();

        for (AnvilSlot slot : items.keySet()) {
            inventory.setItem(slot.getSlot(), items.get(slot));
        }

        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.getContainerId(), Containers.ANVIL, new ChatMessage(title)));
        p.activeContainer = container;
        p.activeContainer.addSlotListener(p);

        inventories.put(player, inventory);
        return inventory;
    }

    public Inventory getPlayersInventory(Player player) {
        return inventories.getOrDefault(player, null);
    }

}
