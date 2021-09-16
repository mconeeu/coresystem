/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.cloud.core.api.world.CloudWorld;
import eu.mcone.cloud.core.api.world.WorldVersion;
import eu.mcone.cloud.core.api.world.WorldVersionType;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.config.CoreJsonConfig;
import eu.mcone.coresystem.api.bukkit.event.world.CoreWorldLoadEvent;
import eu.mcone.coresystem.api.bukkit.event.world.WorldDeleteEvent;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreBlockLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.Region;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.annotation.DontObfuscate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DontObfuscate
public class BukkitCoreWorld implements CoreWorld {

    private String id, name, alias, generator, generatorSettings;
    private int[] version = new int[]{0, 0, 1};
    private WorldType worldType = WorldType.NORMAL;
    private World.Environment environment = World.Environment.NORMAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean generateStructures = false, loadOnStartup = true, autoSave = true, pvp = false, allowAnimals = true, allowMonsters = true, spawnAnimals = false, spawnMonsters = false, keepSpawnInMemory = true;
    private int[] spawnLocation = new int[]{0, 0, 0};

    private Map<String, CoreLocation> locations = new HashMap<>();
    private Map<String, CoreBlockLocation> blockLocations = new HashMap<>();
    private List<Region> regions = new ArrayList<>();
    private List<NpcData> npcData = new ArrayList<>();
    private List<HologramData> hologramData = new ArrayList<>();

    private int configVersion = WorldManager.LATEST_CONFIG_VERSION;

    private transient boolean loaded;
    private transient File directory;

    @Override
    public World bukkit() {
        return Bukkit.getWorld(name);
    }

    @Override
    public void teleport(Player p, String locationName) {
        teleport(p, locationName, true);
    }

    @Override
    public void teleportSilently(Player p, String locationName) {
        teleport(p, locationName, false);
    }

    private void teleport(Player p, String locationName, boolean notify) {
        if (!isLoaded()) {
            load();
            WorldManager.LOADING_BAR.send(p);
        }

        Location loc = getLocation(locationName);
        if (loc != null) {
            if (notify) {
                Msg.send(p, "§2Du wirst teleportiert...");
            }

            p.teleport(loc);
        } else {
            throw new RuntimeCoreException("Could not teleport Player " + p.getName() + " to location " + locationName + ". Location does not exist!");
        }
    }

    @Override
    public Location getLocation(String name) {
        if (name.equalsIgnoreCase("spawn")) {
            return locations.containsKey(name) ? locations.get(name).bukkit() : new Location(bukkit(), spawnLocation[0], spawnLocation[1], spawnLocation[2]);
        } else {
            return locations.containsKey(name) ? locations.get(name).bukkit() : null;
        }
    }

    @Override
    public Location getBlockLocation(String name) {
        return blockLocations.containsKey(name) ? blockLocations.get(name).bukkit() : null;
    }

    @Override
    public Region getRegion(String name) {
        for (Region region : regions) {
            if (region.getName().equals(name)) {
                return region;
            }
        }

        return null;
    }

    @Override
    public void setSpawnLocation(Location loc) {
        this.spawnLocation = new int[]{(int) loc.getX(), (int) loc.getY(), (int) loc.getZ()};
        bukkit().setSpawnLocation(spawnLocation[0], spawnLocation[1], spawnLocation[2]);

        save();
    }

    @Override
    public BukkitCoreWorld setLocation(String name, Location loc) {
        locations.put(name, new CoreLocation(loc));
        return this;
    }

    @Override
    public BukkitCoreWorld setBlockLocation(String name, Location loc) {
        blockLocations.put(name, new CoreBlockLocation(loc));
        return this;
    }

    @Override
    public BukkitCoreWorld setRegion(Region region) {
        regions.removeIf(r -> r.getName().equals(region.getName()));
        regions.add(region);

        return this;
    }

    @Override
    public CoreWorld removeLocation(String name) {
        locations.remove(name);
        return this;
    }

    @Override
    public CoreWorld removeBlockLocation(String name) {
        blockLocations.remove(name);
        return this;
    }

    @Override
    public CoreWorld removeRegion(String name) {
        regions.removeIf(region -> region.getName().equals(name));
        return this;
    }

    @Override
    public List<NPC> getNPCs() {
        List<NPC> result = new ArrayList<>();

        for (NPC npc : CoreSystem.getInstance().getNpcManager().getNpcs()) {
            if (npc.getData().getLocation().bukkit().getWorld().getName().equals(name)) result.add(npc);
        }

        return result;
    }

    @Override
    public NPC getNPC(String name) {
        return CoreSystem.getInstance().getNpcManager().getNPC(this, name);
    }

    @Override
    public List<Hologram> getHolograms() {
        List<Hologram> result = new ArrayList<>();

        for (Hologram hologram : BukkitCoreSystem.getSystem().getHologramManager().getHologramSet()) {
            if (hologram.getData().getLocation().bukkit().getWorld().getName().equals(name)) result.add(hologram);
        }

        return result;
    }

    @Override
    public Hologram getHologram(String name) {
        return CoreSystem.getInstance().getHologramManager().getHologram(this, name);
    }

    @Override
    public void changeName(String name) {
        File directory = getDirectory();
        unload(true);

        if (directory.renameTo(new File(directory.getParent() + File.separator + name))) {
            this.name = name;
            load();
        } else {
            throw new UnsupportedOperationException("Target world folder could not be renamed!");
        }
    }

    @Override
    public boolean load() {
        boolean alreadyLoaded = bukkit() != null;

        if (!alreadyLoaded) {
            BukkitCoreSystem.getInstance().sendConsoleMessage("§fLoading World " + name + "...");

            WorldCreator wc = new WorldCreator(name)
                    .environment(environment)
                    .type(worldType)
                    .generateStructures(generateStructures);

            if (generator != null) {
                wc.generator(generator);
                if (generatorSettings != null)
                    wc.generatorSettings(generatorSettings);
            }

            World w = wc.createWorld();
            Bukkit.getPluginManager().callEvent(new CoreWorldLoadEvent(this, w));
        }

        loaded = true;
        return !alreadyLoaded;
    }

    @Override
    public boolean unload(boolean save) {
        boolean loaded = bukkit() != null;

        if (loaded) {
            World safeWorld = Bukkit.getWorlds().get(0);

            if (!safeWorld.equals(bukkit())) {
                for (Player p : bukkit().getPlayers()) {
                    p.teleport(safeWorld.getSpawnLocation());
                    Msg.send(p, "§7§oDeine aktuelle Welt ist nicht mehr zugänglich. Du wurdest auf die Hauptwelt verschoben.");
                }
            }

            CoreSystem.getInstance().sendConsoleMessage("§fUnloading world " + name + "...");
            Bukkit.unloadWorld(bukkit(), save);
        }

        this.loaded = false;
        return loaded;
    }

    @Override
    public boolean delete() {
        return delete(null);
    }

    @Override
    public boolean delete(Player p) {
        unload(false);

        try {
            FileUtils.deleteDirectory(getDirectory());
            BukkitCoreSystem.getSystem().getWorldManager().getCoreWorlds().remove(this);
            Bukkit.getPluginManager().callEvent(new WorldDeleteEvent(this, p));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void save() {
        setupWorld();

        try {
            File config = new File(getDirectory(), WorldManager.CONFIG_NAME);
            if (!config.exists() && !config.createNewFile()) {
                throw new FileNotFoundException("Config File could not be created!");
            }

            FileOutputStream fos = new FileOutputStream(config);
            Writer fw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            CoreJsonConfig.PRETTY_GSON.toJson(this, getClass(), fw);
            fw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setupWorld() {
        World w = bukkit();

        if (w != null) {
            w.setDifficulty(difficulty);
            w.setSpawnLocation(spawnLocation[0], spawnLocation[1], spawnLocation[2]);
            w.setPVP(pvp);
            w.setKeepSpawnInMemory(keepSpawnInMemory);
            w.setAutoSave(autoSave);
            w.setSpawnFlags(allowAnimals, allowMonsters);

            if (!spawnAnimals) {
                w.setAnimalSpawnLimit(0);
                w.setWaterAnimalSpawnLimit(0);
            }
            if (!spawnMonsters) {
                w.setMonsterSpawnLimit(0);
            }
        } else if (loaded) {
            throw new IllegalStateException("Could not setupWorld " + name + ". World is not loaded but loaded==true! [DynamicWorldLoading==" + WorldManager.DYNAMIC_WORLD_LOADING);
        }
    }

    @Override
    public void purgeAnimals() {
        for (Entity entity : bukkit().getEntities()) {
            if (entity instanceof Squid || entity instanceof Animals) {
                entity.remove();
            }
        }
    }

    @Override
    public void purgeMonsters() {
        for (Entity entity : bukkit().getEntities()) {
            if (entity instanceof Slime || entity instanceof Monster || entity instanceof Ghast || entity instanceof EnderDragon) {
                entity.remove();
            }
        }
    }

    @Override
    public boolean isTracked() {
        return getCloudWorld() != null;
    }

    @Override
    public CloudWorld getCloudWorld() {
        return CoreSystem.getInstance().getWorldManager().getCloudWorldManager().getWorld(id);
    }

    @Override
    public CloudWorld track(UUID initiator) {
        if (!isTracked()) {
            try {
                return CoreSystem.getInstance().getWorldManager().getCloudWorldManager().trackWorld(id, name, initiator);
            } catch (IOException e) {
                throw new IllegalStateException("Could not initiate track of world " + name + " in CloudWorldManager!", e);
            }
        } else throw new IllegalStateException("Could not initiate track of world " + name + ". World is already tracked!");
    }

    @Override
    public WorldVersion commit(WorldVersionType versionType, UUID author, String message) throws IOException {
        CloudWorld world = getCloudWorld();

        if (world != null) {
            WorldVersion newVersion = getCloudWorld().commit(versionType, bukkit().getWorldFolder(), author, message);
            version = newVersion.getVersion();

            return newVersion;
        } else throw new IllegalStateException("Could not commit world " + name + ". This World is not currently tracked with storage backend! Please use CoreWorld#track first.");
    }

    public File getDirectory() {
        return directory != null ? directory : (directory = new File(Bukkit.getWorldContainer(), name));
    }

    @Override
    public String getVersionString() {
        return "v" + version[0] + "." + version[1] + "." + version[2];
    }

    @Override
    public String toString() {
        return CoreSystem.getInstance().getGson().toJson(this, getClass());
    }

}
