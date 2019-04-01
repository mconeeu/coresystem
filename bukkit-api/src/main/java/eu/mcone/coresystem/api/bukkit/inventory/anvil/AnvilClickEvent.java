/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.anvil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@Getter
@RequiredArgsConstructor
public final class AnvilClickEvent {

    private final Player player;
    private final InventoryClickEvent clickEvent;
    private final Inventory anvilInventory;
    private final AnvilSlot slot;
    private final String name;

}
