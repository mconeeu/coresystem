/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.permissions;

import eu.mcone.coresystem.bukkit.CoreSystem;
import org.bukkit.entity.Player;

public class PermissibleBase extends org.bukkit.permissions.PermissibleBase {

    private Player p;

    public PermissibleBase(Player p) {
        super(p);
        this.p = p;
    }

    @Override
    public boolean hasPermission(String permission) {
        return CoreSystem.getCorePlayer(p).hasPermission(permission);
    }

}
