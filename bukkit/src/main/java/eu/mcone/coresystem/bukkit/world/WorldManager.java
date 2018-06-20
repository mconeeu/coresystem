/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.stream.JsonReader;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
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
            File[] dirs = Bukkit.getWorldContainer().listFiles(file -> file.isDirectory() && new File(file, "uid.dat").exists());

            if (dirs != null) {
                for (File dir : dirs) {
                    File config = new File(dir, CONFIG_NAME);
                    World world = Bukkit.getWorld(dir.getName());

                    if (config.exists()) {
                        try (JsonReader reader = new JsonReader(new FileReader(config))) {
                            BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(reader, BukkitCoreWorld.class);
                            reader.close();

                            if (w.isLoadOnStartup()) {
                                if (world == null) {
                                    WorldCreator wc = new WorldCreator(w.getName())
                                            .environment(World.Environment.valueOf(w.getEnvironment()))
                                            .type(WorldType.valueOf(w.getWorldType()))
                                            .generateStructures(w.isGenerateStructures());

                                    if (w.getGenerator() != null) {
                                        wc.generator(w.getGenerator());
                                        if (w.getGeneratorSettings() != null)
                                            wc.generatorSettings(w.getGeneratorSettings());
                                    }

                                    wc.createWorld();
                                }

                                w.save();
                                coreWorlds.add(w);
                                BukkitCoreSystem.getInstance().sendConsoleMessage("§2Loaded World " + w.getName());
                            }
                        }
                    } else {
                        if (world != null) {
                            if (config.createNewFile()) {
                                coreWorlds.add(constructNewCoreWorld(world));
                                BukkitCoreSystem.getInstance().sendConsoleMessage("§2Loaded World " + world.getName());
                            } else {
                                throw new FileNotFoundException("Config File could not be created!");
                            }
                        } else {
                            BukkitCoreSystem.getInstance().sendConsoleMessage("Recognized world "+dir.getName()+" but has no config! Import manually (/world import "+dir.getName()+")");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CoreWorld> getWorlds() {
        return new ArrayList<>(coreWorlds);
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
    public boolean importWorld(String name, World.Environment environment) {
        if (new File(name).exists()) {
            try {
                File uid = new File(name, "level.dat");
                if (uid.exists()) {
                    uid.delete();
                }

                World world = new WorldCreator(name).environment(environment).createWorld();
                File config = new File(name, CONFIG_NAME);

                if (config.exists()) {
                    try (JsonReader reader = new JsonReader(new FileReader(config))) {
                        BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(reader, BukkitCoreWorld.class);
                        w.setupWorld();
                        coreWorlds.add(w);
                    }
                } else {
                    if (config.createNewFile()) {
                        coreWorlds.add(constructNewCoreWorld(world));
                    } else {
                        throw new FileNotFoundException("Config File could not be created!");
                    }
                }

                BukkitCoreSystem.getInstance().sendConsoleMessage("§2Loaded World " + world.getName());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            BukkitCoreSystem.getInstance().sendConsoleMessage("§4Could not import world " + name + "! World does not exist!");
            return false;
        }
    }

    @Override
    public boolean createWorld(String name, Map<String, String> settings) throws IllegalArgumentException {
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
        if (settings.containsKey("keepSpawnInMemory"))
            world.setKeepSpawnInMemory(Boolean.valueOf(settings.get("keepSpawnInMemory")));

        return true;
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
                null,
                world.canGenerateStructures(),
                true,
                world.isAutoSave(),
                world.getPVP(),
                world.getAllowAnimals(),
                world.getAllowMonsters(),
                world.getKeepSpawnInMemory(),
                new int[]{(int) loc.getX(), (int) loc.getY(), (int) loc.getZ()},
                Collections.emptyMap()
        );
        w.save();

        return w;
    }

}
