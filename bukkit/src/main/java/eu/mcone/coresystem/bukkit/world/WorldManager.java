/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldCreateProperties;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.LocationCMD;
import eu.mcone.coresystem.bukkit.command.WorldCMD;
import org.bukkit.*;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorldManager implements eu.mcone.coresystem.api.bukkit.world.WorldManager {

    final static String CONFIG_NAME = "core-config.json";

    private final static String CONFIG_VERSION_KEY = "configVersion";
    private final static int LATEST_CONFIG_VERSION = 3;

    private WorldCMD worldCMD;
    List<BukkitCoreWorld> coreWorlds;

    public WorldManager(BukkitCoreSystem instance) {
        this.coreWorlds = new ArrayList<>();
        this.worldCMD = new WorldCMD();

        instance.getPluginManager().registerCoreCommand(worldCMD, CoreSystem.getInstance());
        instance.getPluginManager().registerCoreCommand(new LocationCMD(instance), CoreSystem.getInstance());

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
                        try (FileReader reader = new FileReader(config)) {
                            JsonElement json = BukkitCoreSystem.getSystem().getJsonParser().parse(reader);
                            reader.close();

                            if (!json.getAsJsonObject().has(CONFIG_VERSION_KEY)) {
                                json.getAsJsonObject().addProperty(CONFIG_VERSION_KEY, 0);
                            }
                            if (json.getAsJsonObject().get(CONFIG_VERSION_KEY).getAsInt() < LATEST_CONFIG_VERSION) {
                                json = migrateConfig(json);
                            }

                            BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(json, BukkitCoreWorld.class);

                            if (w.isLoadOnStartup()) {
                                if (world == null) {
                                    WorldCreator wc = new WorldCreator(w.getName())
                                            .environment(w.getEnvironment())
                                            .type(w.getWorldType())
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
                            coreWorlds.add(constructNewCoreWorld(world, null, null));
                            BukkitCoreSystem.getInstance().sendConsoleMessage("§2Loaded World " + world.getName());
                        } else {
                            BukkitCoreSystem.getInstance().sendConsoleMessage("Recognized world " + dir.getName() + " but has no config! Import manually (/world import " + dir.getName() + ")");
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
    public void enableUploadCommand(boolean enable) {
        worldCMD.setEnableUploadCmd(enable);
    }

    @Override
    public boolean importWorld(String name, World.Environment environment) {
        if (new File(name).exists()) {
            try {
                File uid = new File(name, "uid.dat");
                if (uid.exists()) uid.delete();

                World world = new WorldCreator(name).environment(environment).createWorld();
                File config = new File(name, CONFIG_NAME);

                if (world != null) {
                    if (config.exists()) {
                        try (JsonReader reader = new JsonReader(new FileReader(config))) {
                            BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(reader, BukkitCoreWorld.class);
                            w.setupWorld();
                            coreWorlds.add(w);
                        }
                    } else {
                        coreWorlds.add(constructNewCoreWorld(world, null, null));
                    }

                    BukkitCoreSystem.getInstance().sendConsoleMessage("§2Loaded World " + world.getName());
                    return true;
                } else {
                    BukkitCoreSystem.getInstance().sendConsoleMessage("§4Could not import world" + name + "! WorldCreator returned null!");
                    return false;
                }
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
    public World createWorld(String name, WorldCreateProperties properties) throws IllegalArgumentException {
        WorldCreator wc = new WorldCreator(name);

        if (properties.getSeed() != 0) wc.seed(properties.getSeed());
        if (properties.getWorldType() != null) wc.type(properties.getWorldType());
        if (properties.getEnvironment() != null) wc.environment(properties.getEnvironment());
        if (properties.getGenerator() != null) {
            wc.generator(properties.getGenerator());
            if (properties.getGeneratorSettings() != null) wc.generatorSettings(properties.getGeneratorSettings());
        }
        wc.generateStructures(properties.isGenerateStructures());

        World world = wc.createWorld();
        if (properties.getDifficulty() != null) world.setDifficulty(properties.getDifficulty());
        world.setAutoSave(properties.isAutoSave());
        world.setPVP(properties.isPvp());
        world.setSpawnFlags(properties.isAllowAnimals(), properties.isAllowMonsters());
        if (!properties.isSpawnAnimals()) {
            world.setAnimalSpawnLimit(0);
            world.setWaterAnimalSpawnLimit(0);
        }
        if (!properties.isSpawnMonsters()) {
            world.setMonsterSpawnLimit(0);
        }
        world.setKeepSpawnInMemory(properties.isKeepSpawnInMemory());

        coreWorlds.add(constructNewCoreWorld(world, properties.getGenerator(), properties.getGeneratorSettings()));
        return world;
    }

    private BukkitCoreWorld constructNewCoreWorld(World world, String generator, String generatorSettings) {
        Location loc = world.getSpawnLocation();
        BukkitCoreWorld w = new BukkitCoreWorld(
                world.getName(),
                world.getName(),
                generator,
                generatorSettings,
                world.getWorldType(),
                world.getEnvironment(),
                world.getDifficulty(),
                world.canGenerateStructures(),
                true,
                world.isAutoSave(),
                world.getPVP(),
                world.getAllowAnimals(),
                world.getAllowMonsters(),
                world.getAnimalSpawnLimit() > 0,
                world.getMonsterSpawnLimit() > 0,
                world.getKeepSpawnInMemory(),
                new int[]{(int) loc.getX(), (int) loc.getY(), (int) loc.getZ()},
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyList(),
                Collections.emptyList(),
                LATEST_CONFIG_VERSION
        );
        w.save();

        return w;
    }

    private static JsonElement migrateConfig(JsonElement json) {
        switch (json.getAsJsonObject().get(CONFIG_VERSION_KEY).getAsInt()) {
            case 0: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 0 to 1...");
                for (JsonElement e : json.getAsJsonObject().get("npcData").getAsJsonArray()) {
                    if (!e.getAsJsonObject().has("skinKind") || e.getAsJsonObject().get("skinKind") == null) {
                        e.getAsJsonObject().addProperty("skinKind", SkinInfo.SkinType.DATABASE.toString());
                    }
                }
            }
            case 1: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 1 to 2...");

                //Updating npcData
                for (JsonElement e : json.getAsJsonObject().get("npcData").getAsJsonArray()) {
                    JsonObject o = e.getAsJsonObject();

                    PlayerNpcData data = new PlayerNpcData();
                    data.setSkinName(o.get("skinName").getAsString());
                    data.setSkinType(SkinInfo.SkinType.valueOf(o.get("skinKind").getAsString()));

                    o.addProperty("type", EntityType.PLAYER.toString());
                    o.remove("skinName");
                    o.remove("skinKind");
                    o.add("entityData", CoreSystem.getInstance().getGson().toJsonTree(data));
                }

                //Updating all locations
                String worldName = json.getAsJsonObject().get("name").getAsString();
                for (Map.Entry<String, JsonElement> e : json.getAsJsonObject().get("locations").getAsJsonObject().entrySet()) {
                    json.getAsJsonObject().get("locations").getAsJsonObject().add(e.getKey(), insertWorldVar(e.getValue().getAsJsonObject(), worldName));
                }
                for (JsonElement e : json.getAsJsonObject().get("npcData").getAsJsonArray()) {
                    e.getAsJsonObject().add("location", insertWorldVar(e.getAsJsonObject().get("location").getAsJsonObject(), worldName));
                }
                for (JsonElement e : json.getAsJsonObject().get("hologramData").getAsJsonArray()) {
                    e.getAsJsonObject().add("location", insertWorldVar(e.getAsJsonObject().get("location").getAsJsonObject(), worldName));
                }
            }
            case 2: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 2 to 3...");

                if (json.getAsJsonObject().has("allowAnimals")) {
                    json.getAsJsonObject().addProperty("spawnAnimals", json.getAsJsonObject().get("allowAnimals").getAsBoolean());
                    json.getAsJsonObject().addProperty("allowAnimals", true);
                }
                if (json.getAsJsonObject().has("allowMonsters")) {
                    json.getAsJsonObject().addProperty("spawnMonsters", json.getAsJsonObject().get("allowMonsters").getAsBoolean());
                    json.getAsJsonObject().addProperty("allowMonsters", true);
                }
            }
        }

        json.getAsJsonObject().addProperty(CONFIG_VERSION_KEY, LATEST_CONFIG_VERSION);
        return json;
    }

    private static JsonObject insertWorldVar(JsonObject o, String worldName) {
        JsonObject result = new JsonObject();
        result.addProperty("world", worldName);

        for (Map.Entry<String, JsonElement> e : o.entrySet()) {
            result.add(e.getKey(), e.getValue());
        }

        return result;
    }

}
