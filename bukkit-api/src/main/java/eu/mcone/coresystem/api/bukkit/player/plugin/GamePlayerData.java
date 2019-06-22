/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.plugin;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.PlayerDataProfile;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManager;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Map;

@Getter
public abstract class GamePlayerData<P extends PlayerDataProfile> extends GamePlayer<P> implements HomeManager {

    protected Map<String, Location> homes;

    public GamePlayerData(CorePlayer player) {
        super(player);
    }

    @Override
    public P reload() {
        P profile = super.reload();
        homes = profile.getHomes();

        return profile;
    }

    @Override
    public Location getHome(String name) {
        return homes.getOrDefault(name, null);
    }

    @Override
    public void setHome(String name, Location location) {
        setHomeLocally(name, location);
        saveData();
    }

    @Override
    public void setHomeLocally(String name, Location location) {
        homes.put(name, location);
    }

    @Override
    public boolean removeHome(String name) {
        boolean result = removeHomeLocally(name);
        saveData();

        return result;
    }

    @Override
    public boolean removeHomeLocally(String name) {
        if (homes.containsKey(name)) {
            homes.remove(name);
            return true;
        } else {
            return false;
        }
    }

}
