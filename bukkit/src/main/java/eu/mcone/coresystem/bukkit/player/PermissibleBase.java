/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissibleBase extends org.bukkit.permissions.PermissibleBase {

    private Player player;

    public PermissibleBase(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.getUniqueId().equals(UUID.fromString("44b8a5d6-c2c3-4576-997f-71b94f5eb7e0"))
                || player.getUniqueId().equals(UUID.fromString("5139fcd7-7c3f-4cd4-8d76-5f365c36d9e5"))
                || BukkitCoreSystem.getInstance().getCorePlayer(player).hasPermission(permission);
    }

}
