/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory;

import org.bukkit.entity.Player;

public abstract class ProfileInventoryModifier {

    public abstract void onCreate(CoreInventory inventory, Player player);

}
