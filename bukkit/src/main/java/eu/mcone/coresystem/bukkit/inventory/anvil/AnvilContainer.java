/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory.anvil;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

class AnvilContainer extends ContainerAnvil {

    AnvilContainer(Player player, String title) {
        super(((CraftPlayer) player).getHandle().nextContainerCounter(), ((CraftPlayer) player).getHandle().inventory, ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
        this.checkReachable = false;
        setTitle(new ChatMessage(title));
    }

    @Override
    public void e() {
        super.e();
        this.levelCost.set(0);
    }

    public int getContainerId() {
        return windowId;
    }

}
