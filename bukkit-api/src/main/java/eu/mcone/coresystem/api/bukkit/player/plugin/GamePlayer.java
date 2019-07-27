/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.plugin;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.GameProfile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class GamePlayer<P extends GameProfile> {

    @Getter
    protected final CorePlayer corePlayer;
    
    public GamePlayer(CorePlayer player) {
        this.corePlayer = player;
        reload();
    }

    public P reload() {
        return loadData();
    }

    /**
     * Use CorePlugin#loadGameProfile(CorePlayer, Class<? extends GameProfile>) to return your custom GameProfile here
     * @return custom GameProfile instance
     */
    protected abstract P loadData();

    /**
     * Use CorePlugin#saveGameProfile(GameProfile) to save a constructed GameProfile (with the data of this object) to database
     */
    protected abstract void saveData();
    
    public Player bukkit() {
        return Bukkit.getPlayer(corePlayer.getUuid());
    }
    
}
