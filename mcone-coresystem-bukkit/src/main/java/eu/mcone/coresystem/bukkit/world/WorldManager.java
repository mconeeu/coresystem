/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.IOException;

public class WorldManager implements eu.mcone.coresystem.api.bukkit.world.WorldManager {

    private BukkitCoreSystem instance;

    public WorldManager(BukkitCoreSystem instance) {
        this.instance = instance;
    }

    public void addWorld(String name, World.Environment environment) {
        WorldCreator wc = new WorldCreator(name).environment(environment);
        wc.createWorld();
    }

    public boolean removeWorld(World world) {
        try {
            instance.getServer().unloadWorld(world, false);
            FileUtils.deleteDirectory(world.getWorldFolder());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
