/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.modification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
class UniqueItemStack implements Serializable {

    private final UUID uuid;
    private final ItemStack itemStack;

}
