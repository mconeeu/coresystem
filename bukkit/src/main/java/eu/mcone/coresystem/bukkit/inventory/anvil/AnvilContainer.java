/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.anvil;

import net.minecraft.server.v1_8_R3.*;

class AnvilContainer extends ContainerAnvil {

    AnvilContainer(EntityHuman entity) {
        super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

}
