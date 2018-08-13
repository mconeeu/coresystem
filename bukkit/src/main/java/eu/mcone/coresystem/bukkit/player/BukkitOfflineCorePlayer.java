/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;

public class BukkitOfflineCorePlayer extends GlobalOfflineCorePlayer implements OfflineCorePlayer {

    public BukkitOfflineCorePlayer(GlobalCoreSystem instance, String name) throws PlayerNotResolvedException {
        super(instance, name);
    }

    @Override
    public OfflineCorePlayer loadPermissions() {
        this.permissions = BukkitCoreSystem.getSystem().getPermissionManager().getPermissions(uuid.toString(), groupSet);
        return this;
    }

    @Override
    public boolean hasPermission(String permission) {
        return instance.getPermissionManager().hasPermission(permissions, permission);
    }

}
