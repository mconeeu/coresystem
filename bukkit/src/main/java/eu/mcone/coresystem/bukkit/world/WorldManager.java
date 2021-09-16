/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.mcone.cloud.core.api.world.CloudWorldManager;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.world.WorldCreateEvent;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldCreateProperties;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.LocationCMD;
import eu.mcone.coresystem.bukkit.command.RegionCMD;
import eu.mcone.coresystem.bukkit.command.WorldCMD;
import eu.mcone.coresystem.bukkit.listener.WorldListener;
import group.onegaming.networkmanager.core.api.database.Database;
import group.onegaming.networkmanager.core.api.random.UniqueIdType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager implements eu.mcone.coresystem.api.bukkit.world.WorldManager {

    private static final String CONFIG_VERSION_KEY = "configVersion";
    static final String CONFIG_NAME = "core-config.json";
    static final int LATEST_CONFIG_VERSION = 8;

    @Getter
    private final CoreDynamicWorldLoader dynamicWorldLoader;
    @Getter
    private final CloudWorldManager cloudWorldManager;
    @Getter
    private final List<BukkitCoreWorld> coreWorlds;

    public WorldManager(BukkitCoreSystem instance) {
        this.coreWorlds = new ArrayList<>();
        this.cloudWorldManager = new CloudWorldManager(instance.getMongoDB(Database.CLOUD), "https://storage.mcone.eu", instance.getJsonParser());

        instance.registerCommands(new WorldCMD(this), new LocationCMD(instance), new RegionCMD(this));
        instance.registerEvents(new WorldListener(this));

        if (DYNAMIC_WORLD_LOADING) {
            instance.sendConsoleMessage("§2Enabling DynamicWorldLoader...");
            this.dynamicWorldLoader = new CoreDynamicWorldLoader(instance, this);
        } else {
            this.dynamicWorldLoader = null;
        }

        reload();
    }

    @Override
    public void reload() {
        this.coreWorlds.clear();

        try {
            File[] dirs = Bukkit.getWorldContainer().listFiles(file -> file.isDirectory() && new File(file, "uid.dat").exists());

            for (File dir : dirs) {
                File config = new File(dir, CONFIG_NAME);
                World world = Bukkit.getWorld(dir.getName());

                if (config.exists()) {
                    FileInputStream fis = new FileInputStream(config);
                    InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    JsonElement json = BukkitCoreSystem.getSystem().getJsonParser().parse(reader);
                    reader.close();
                    fis.close();

                    if (json.getAsJsonObject().has(CONFIG_VERSION_KEY)) {
                        if (json.getAsJsonObject().get(CONFIG_VERSION_KEY).getAsInt() < LATEST_CONFIG_VERSION) {
                            System.out.println("current config version: "+json.getAsJsonObject().get(CONFIG_VERSION_KEY).getAsInt());
                            json = migrateConfig(json);
                        }

                        BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(json, BukkitCoreWorld.class);

                        if (w.getName().equals(dir.getName())) {
                            w.save();

                            if (w.isLoadOnStartup()) {
                                if (!DYNAMIC_WORLD_LOADING) {
                                    w.load();
                                }

                                coreWorlds.add(w);
                                BukkitCoreSystem.getInstance().sendConsoleMessage("§2" + (w.isLoaded() ? "Loaded" : "Recognized") + " World " + w.getName() + " " + w.getVersionString() + " (" + w.getId() + ") ");
                            }
                        } else {
                            BukkitCoreSystem.getInstance().sendConsoleMessage("§cCould not load World "+dir.getName()+". World name in core-config ("+w.getName()+") does not match!");
                        }

                        continue;
                    }
                }

                if (world != null) {
                    coreWorlds.add(constructNewCoreWorld(world, null, null));
                    BukkitCoreSystem.getInstance().sendConsoleMessage("§2Loaded World " + world.getName());
                } else {
                    BukkitCoreSystem.getInstance().sendConsoleMessage("Recognized world " + dir.getName() + " but has no config! Import manually (/world import " + dir.getName() + ")");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not reload Worlds", e);
        }
    }

    @Override
    public List<CoreWorld> getWorlds() {
        return new ArrayList<>(coreWorlds);
    }

    @Override
    public CoreWorld getWorldById(String id) {
        for (BukkitCoreWorld w : coreWorlds) {
            if (w.getId().equals(id)) {
                return w;
            }
        }
        return null;
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
    public boolean existWorld(String name) {
        return new File(Bukkit.getWorldContainer(), name).exists();
    }

    @Override
    public boolean importWorld(String name, World.Environment environment) {
        return importWorld(name, environment, null);
    }

    @Override
    public boolean importWorld(String name, World.Environment environment, Player p) {
        if (new File(name).exists()) {
            try {
                File uid = new File(name, "uid.dat");
                if (uid.exists()) uid.delete();

                World world = new WorldCreator(name).environment(environment).createWorld();
                File config = new File(name, CONFIG_NAME);

                if (world != null) {
                    if (config.exists()) {
                        FileInputStream fis = new FileInputStream(config);
                        InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                        BukkitCoreWorld w = CoreSystem.getInstance().getGson().fromJson(reader, BukkitCoreWorld.class);
                        reader.close();
                        fis.close();

                        w.setupWorld();
                        coreWorlds.add(w);
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
    public World createWorld(String name, WorldCreateProperties properties) {
        return createWorld(name, properties, null);
    }

    @Override
    public World createWorld(String name, WorldCreateProperties properties, Player p) throws IllegalArgumentException {
        WorldCreator wc = new WorldCreator(name);

        if (properties.getSeed() != 0) wc.seed(properties.getSeed());
        if (properties.getWorldType() != null) wc.type(properties.getWorldType());
        if (properties.getEnvironment() != null) wc.environment(properties.getEnvironment());
        if (properties.getGenerator() != null) wc.generator(properties.getGenerator());
        if (properties.getGeneratorSettings() != null) wc.generatorSettings(properties.getGeneratorSettings());
        wc.generateStructures(properties.isGenerateStructures());

        if (p != null) {
            Msg.sendSuccess(p, "Die Welt !["+name+"] wird erstellt! Dies kann ein paar Sekunden dauern...");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != p) {
                if (player.hasPermission("group.team")) {
                    Msg.sendWarning(player, "!["+(p != null ? p.getName() : "Der Server")+"] erstellt eine Neue Welt! Dadurch können Lags entstehen...");
                } else {
                    Msg.sendWarning(player, "Der Server bearbeitet in den nächsten Sekunden eine leistungsintensive Aufgabe, wodurch Lags verursacht werden können!");
                }
            }
        }

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

        BukkitCoreWorld w = constructNewCoreWorld(world, properties.getGenerator(), properties.getGeneratorSettings());
        coreWorlds.add(w);
        Bukkit.getPluginManager().callEvent(new WorldCreateEvent(w, p));
        return world;
    }

    public void disable() {
        if (DYNAMIC_WORLD_LOADING) {
            this.dynamicWorldLoader.disable();
        }
    }

    private BukkitCoreWorld constructNewCoreWorld(World world, String generator, String generatorSettings) {
        Location loc = world.getSpawnLocation();
        BukkitCoreWorld w = new BukkitCoreWorld(
                getUniqueWorldId(),
                world.getName(),
                world.getName(),
                generator,
                generatorSettings,
                new int[]{0, 0, 1},
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
                new HashMap<>(),
                new HashMap<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                LATEST_CONFIG_VERSION,
                true,
                new File(Bukkit.getWorldContainer(), world.getName())
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
            case 3: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 3 to 4...");

                //Updating PlayerNpcData
                for (JsonElement e : json.getAsJsonObject().get("npcData").getAsJsonArray()) {
                    //Remove tempLocations
                    if (e.getAsJsonObject().has("tempLocation")) {
                        e.getAsJsonObject().remove("tempLocation");
                    }

                    if (e.getAsJsonObject().get("type").getAsString().equals("PLAYER")) {
                        JsonObject entityData = e.getAsJsonObject().get("entityData").getAsJsonObject();

                        if (entityData.has("equipment")) {
                            entityData.add("equipment", new JsonObject());
                        }
                    }
                }
            }
            case 4:
            case 5: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 4/5 to 6...");

                JsonObject world = json.getAsJsonObject();
                JsonArray version = new JsonArray();
                version.add(new JsonPrimitive(0));
                version.add(new JsonPrimitive(0));
                version.add(new JsonPrimitive(1));

                world.remove("ID");
                world.remove("iD");
                world.add("version", version);
            }
            case 6: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 6 to 7...");
                json.getAsJsonObject().add("regions", new JsonArray());
            }
            case 7: {
                CoreSystem.getInstance().sendConsoleMessage("§7Updating Config from version 7 to 8...");
                json.getAsJsonObject().addProperty("id", getUniqueWorldId());
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

    private static String getUniqueWorldId() {
        return BukkitCoreSystem.getSystem().getUniqueIdUtil().getUniqueKey("world", UniqueIdType.STRING);
    }

}
