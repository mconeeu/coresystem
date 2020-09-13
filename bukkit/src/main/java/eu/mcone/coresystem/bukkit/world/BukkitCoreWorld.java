/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.config.CoreJsonConfig;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreBlockLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DontObfuscate
public class BukkitCoreWorld implements CoreWorld {

    private String ID, name, alias, generator, generatorSettings;
    private WorldType worldType = WorldType.NORMAL;
    private World.Environment environment = World.Environment.NORMAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean generateStructures = false, loadOnStartup = true, autoSave = true, pvp = false, allowAnimals = true, allowMonsters = true, spawnAnimals = false, spawnMonsters = false, keepSpawnInMemory = true;
    private int[] spawnLocation = new int[]{0, 0, 0};

    private Map<String, CoreLocation> locations = new HashMap<>();
    private Map<String, CoreBlockLocation> blockLocations = new HashMap<>();
    private List<NpcData> npcData = new ArrayList<>();
    private List<HologramData> hologramData = new ArrayList<>();

    private int configVersion = WorldManager.LATEST_CONFIG_VERSION;

    @Override
    public World bukkit() {
        return Bukkit.getWorld(name);
    }

    @Override
    public void teleport(Player p, String locationName) {
        Location loc = getLocation(locationName);

        if (loc != null) {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§2Du wirst teleportiert...");
            p.teleport(loc);
        } else {
            BukkitCoreSystem.getInstance().getMessenger().send(p, "§4Dieser Ort existiert nicht.");
        }
    }

    @Override
    public void teleportSilently(Player p, String locationName) {
        Location loc = getLocation(locationName);
        if (getLocation(locationName) != null) {
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
        File folder = bukkit().getWorldFolder();
        unload(true);

        if (folder.renameTo(new File(folder.getParent() + File.separator + name))) {
            WorldCreator wc = new WorldCreator(name)
                    .environment(environment)
                    .type(worldType)
                    .generateStructures(generateStructures);

            if (generator != null) {
                wc.generator(generator);
                if (generatorSettings != null) wc.generatorSettings(generatorSettings);
            }

            wc.createWorld();
            this.name = name;
            save();
        } else {
            throw new UnsupportedOperationException("Target world folder could not be renamed!");
        }
    }

    @Override
    public void unload(boolean save) {
        World safeWorld = Bukkit.getWorlds().get(0);

        if (!safeWorld.equals(bukkit())) {
            for (Player p : bukkit().getPlayers()) {
                p.teleport(safeWorld.getSpawnLocation());
                BukkitCoreSystem.getInstance().getMessenger().send(p, "§7§oDeine aktuelle Welt ist nicht mehr zugänglich. Du wurdest auf die Hauptwelt verschoben.");
            }
        }

        Bukkit.unloadWorld(bukkit(), save);
        BukkitCoreSystem.getSystem().getWorldManager().coreWorlds.remove(this);
    }

    @Override
    public boolean delete() {
        File worldFolder = bukkit().getWorldFolder();
        unload(false);

        try {
            FileUtils.deleteDirectory(worldFolder);
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
            File config = new File(bukkit().getWorldFolder(), WorldManager.CONFIG_NAME);
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
    public String toString() {
        return CoreSystem.getInstance().getGson().toJson(this, getClass());
    }

}
