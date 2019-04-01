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
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AnvilInventory implements CoreAnvilInventory {

    @Getter
    private AnvilClickEventHandler handler;
    private Map<AnvilSlot, ItemStack> items;
    private Map<Player, Inventory> inventories;

    public AnvilInventory(AnvilClickEventHandler handler) {
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
        AnvilContainer container = new AnvilContainer(p);
        Inventory inventory = container.getBukkitView().getTopInventory();

        for (AnvilSlot slot : items.keySet()) {
            inventory.setItem(slot.getSlot(), items.get(slot));
        }

        int c = p.nextContainerCounter();

        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
        p.activeContainer = container;
        p.activeContainer.windowId = c;
        p.activeContainer.addSlotListener(p);

        inventories.put(player, inventory);
        return inventory;
    }

    public Inventory getPlayersInventory(Player player) {
        return inventories.getOrDefault(player, null);
    }

    @Override
    public AnvilInventory destroy() {
        this.handler = null;
        this.items = null;

        return this;
    }

}
