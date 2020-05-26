/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.plugin;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.PlayerInventoryProfile;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.EnderchestManager;
import lombok.Getter;
import org.bukkit.inventory.Inventory;

@Getter
public abstract class GamePlayerInventory<P extends PlayerInventoryProfile> extends GamePlayerData<P> implements EnderchestManager<P> {

    protected Inventory enderchest;

    public GamePlayerInventory(CorePlayer player) {
        super(player);
    }

    @Override
    public P reload() {
        P profile = super.reload();

        this.enderchest = profile.getEnderchest();

        if (profile.isSizeChange()) {
            saveData();
        }

        return profile;
    }

    @Override
    public void updateEnderchest(Inventory enderchest) {
        this.enderchest = enderchest;
        saveData();
    }

}
