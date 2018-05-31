/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.stream.JsonReader;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldProperties;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.WorldCMD;
import org.bukkit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorldManager implements eu.mcone.coresystem.api.bukkit.world.WorldManager {

    final static String CONFIG_NAME = "core-config.json";

    private List<BukkitCoreWorld> coreWorlds;

    public WorldManager(BukkitCoreSystem instance) {
        this.coreWorlds = new ArrayList<>();

        instance.getCommand("world").setExecutor(new WorldCMD());
        reload();
    }

    @Override
    public void reload() {
        this.coreWorlds.clear();

        try {
            for (World world : Bukkit.getWorlds()) {
                final File config = new File(world.getWorldFolder(), CONFIG_NAME);

                if (config.exists()) {
                    try (JsonReader reader = new JsonReader(new FileReader(config))) {
                        BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(reader, BukkitCoreWorld.class);
                        setupWorld(world, w);
                        coreWorlds.add(w);
                    }
                } else {
                    CoreSystem.getInstance().sendConsoleMessage("Missing config file in world "+world.getName()+". Creating...");

                    if (config.createNewFile()) {
                        constructNewCoreWorld(world);
                    } else {
                        throw new FileNotFoundException("Config File could not be created!");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CoreWorld getWorld(String name) {
        for (BukkitCoreWorld w : coreWorlds) {
            if (w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }
        return null;
    }

    @Override
    public CoreWorld getWorld(World world) {
        for (BukkitCoreWorld w : coreWorlds) {
            if (w.getName().equalsIgnoreCase(world.getName())) {
                return w;
            }
        }
        return null;
    }



    @Override
    public boolean addWorld(String name, World.Environment environment) {
        try {
            new WorldCreator(name).environment(environment).createWorld();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addWorld(String name, Map<String, String> settings) throws IllegalArgumentException {
        WorldCreator wc = new WorldCreator(name);

        try {
            if (settings.containsKey("seed")) wc.seed(Long.valueOf(settings.get("seed")));
            if (settings.containsKey("type")) wc.type(WorldType.valueOf(settings.get("type")));
            if (settings.containsKey("environment"))
                wc.environment(World.Environment.valueOf(settings.get("environment")));
            if (settings.containsKey("generator")) {
                wc.generator(settings.get("generator"));
                if (settings.containsKey("generatorSettings")) wc.generatorSettings(settings.get("generatorSettings"));
            }
            if (settings.containsKey("generateStructures"))
                wc.generateStructures(Boolean.valueOf(settings.get("generateStructures")));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }

        World world = wc.createWorld();
        if (settings.containsKey("difficulty")) world.setDifficulty(Difficulty.valueOf(settings.get("difficulty")));
        if (settings.containsKey("autoSave")) world.setAutoSave(Boolean.valueOf(settings.get("autoSave")));
        if (settings.containsKey("pvp")) world.setPVP(Boolean.valueOf(settings.get("pvp")));
        if (settings.containsKey("allowAnimals") && !Boolean.valueOf("allowAnimals")) {
            world.setAnimalSpawnLimit(0);
            world.setWaterAnimalSpawnLimit(0);
        }
        if (settings.containsKey("allowMonsters") && !Boolean.valueOf("allowMonsters")) {
            world.setMonsterSpawnLimit(0);
        }
        if (settings.containsKey("keepSpawnInMemory")) world.setKeepSpawnInMemory(Boolean.valueOf(settings.get("keepSpawnInMemory")));

        return true;
    }

    void setupWorld(World w, BukkitCoreWorld cw) {
        w.setDifficulty(Difficulty.valueOf(cw.getDifficulty()));
        w.setSpawnLocation(cw.getSpawnLocation()[0], cw.getSpawnLocation()[1], cw.getSpawnLocation()[2]);
        w.setPVP(cw.getProperties().isPvp());
        w.setKeepSpawnInMemory(cw.getProperties().isKeepSpawnInMemory());

        if (!cw.getProperties().isAllowAnimals()) {
            w.setAnimalSpawnLimit(0);
            w.setWaterAnimalSpawnLimit(0);
        }
        if (!cw.getProperties().isAllowMonsters()) {
            w.setMonsterSpawnLimit(0);
        }
    }

    private BukkitCoreWorld constructNewCoreWorld(World world) {
        Location loc = world.getSpawnLocation();
        BukkitCoreWorld w = new BukkitCoreWorld(
                world.getName(),
                world.getWorldType().toString(),
                world.getEnvironment().toString(),
                world.getDifficulty().toString(),
                null,
                null,
                world.canGenerateStructures(),
                new WorldProperties(
                        world.isAutoSave(),
                        world.getPVP(),
                        world.getAllowAnimals(),
                        world.getAllowMonsters(),
                        world.getKeepSpawnInMemory()
                ),
                new int[] {(int) loc.getX(), (int) loc.getY(), (int) loc.getZ()},
                Collections.emptyMap()
        );
        w.save();

        coreWorlds.add(w);
        return w;
    }

}
