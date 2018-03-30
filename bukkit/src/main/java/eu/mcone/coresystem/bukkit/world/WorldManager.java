/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.IOException;

public class WorldManager {

    public static void addWorld(String name, World.Environment environment) {
        WorldCreator wc = new WorldCreator(name).environment(environment);
        wc.createWorld();
    }

    public static boolean removeWorld(World world) {
        try {
            Bukkit.unloadWorld(world, false);
            FileUtils.deleteDirectory(world.getWorldFolder());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
