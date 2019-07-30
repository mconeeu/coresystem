/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.anvil;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.ContainerAnvil;
import net.minecraft.server.v1_13_R2.EntityHuman;

class AnvilContainer extends ContainerAnvil {

    AnvilContainer(EntityHuman entity) {
        super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return true;
    }

}
