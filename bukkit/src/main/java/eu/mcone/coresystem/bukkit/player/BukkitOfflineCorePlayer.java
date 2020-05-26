/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;

import java.util.UUID;

public class BukkitOfflineCorePlayer extends GlobalOfflineCorePlayer implements OfflineCorePlayer {

    public BukkitOfflineCorePlayer(GlobalCoreSystem instance, UUID uuid) throws PlayerNotResolvedException {
        super(instance, uuid, false);
    }

    public BukkitOfflineCorePlayer(GlobalCoreSystem instance, String name) throws PlayerNotResolvedException {
        super(instance, name, false);
    }

    public SkinInfo getSkin() {
        return CoreSystem.getInstance().getPlayerUtils().getSkinInfo(uuid);
    }

}
