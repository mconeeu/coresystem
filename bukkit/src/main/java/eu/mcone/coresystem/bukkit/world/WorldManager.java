/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import eu.mcone.cloud.core.server.world.WorldProperties;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.IOException;

public class WorldManager {

    public static World addWorld(String name, WorldType worldType, World.Environment environment, Difficulty difficulty, String spawnLocation, String generator, WorldProperties properties) {
        WorldCreator wc = new WorldCreator(name)
                .environment(environment)
                .type(worldType)
                .generateStructures(properties.isGenerateStructures());

        if (generator != null) wc.generator(generator);

        World world = wc.createWorld();
        world.setDifficulty(difficulty);
        JsonArray loc = new JsonParser().parse(spawnLocation).getAsJsonArray();
        world.setSpawnLocation(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt());
        world.setPVP(properties.isPvp());
        world.setAutoSave(properties.isAutoSave());
        world.setKeepSpawnInMemory(properties.isKeepSpawnInMemory());

        if (!properties.isAllowAnimals()) {
            world.setAnimalSpawnLimit(0);
            world.setWaterAnimalSpawnLimit(0);
        }
        if (!properties.isAllowMonsters()) {
            world.setMonsterSpawnLimit(0);
        }

        return world;
    }

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
