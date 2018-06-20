/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import com.google.gson.stream.JsonWriter;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldProperties;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.annotation.DontObfuscate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.Diff;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.omg.CORBA.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
@Getter @Setter
@DontObfuscate
public class BukkitCoreWorld implements CoreWorld {

    private String name, worldType = "NORMAL", environment = "NORMAL", difficulty = "NORMAL", generator, generatorSettings, templateName;
    private boolean generateStructures = false, loadOnStartup = true, autoSave = true, pvp, allowAnimals, allowMonsters, keepSpawnInMemory = true;
    private int[] spawnLocation = new int[]{0, 0, 0};

    private Map<String, CoreLocation> locations;

    //this constructor is needed!
    public BukkitCoreWorld() {}

    @Override
    public World bukkit() {
        return Bukkit.getWorld(name);
    }

    @Override
    public void setWorldType(WorldType worldType) {
        this.worldType = worldType.toString();
    }

    @Override
    public void setEnvironment(World.Environment environment) {
        this.environment = environment.toString();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty.toString();
    }

    @Override
    public void teleport(Player p, String locationName) {
        CoreLocation loc = getLocation(locationName);

        if (loc != null) {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du wirst teleportiert...");
            p.teleport(loc.bukkit());
        } else {
            BukkitCoreSystem.getInstance().getMessager().send(p, "§4Dieser Ort existiert nicht.");
        }
    }

    @Override
    public void teleportSilently(Player p, String locationName) {
        CoreLocation loc = getLocation(locationName);
        if (getLocation(locationName) != null) p.teleport(loc.bukkit());
    }

    @Override
    public CoreLocation getLocation(String name) {
        return locations.getOrDefault(name, null);
    }

    @Override
    public void setSpawnLocation(Location loc) {
        this.spawnLocation = new int[]{(int) loc.getX(), (int) loc.getY(), (int) loc.getZ()};
        bukkit().setSpawnLocation(spawnLocation[0], spawnLocation[1], spawnLocation[2]);
    }

    @Override
    public BukkitCoreWorld setLocation(String name, Location loc) {
        locations.put(name, new CoreLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
        return this;
    }

    @Override
    public CoreWorld removeLocation(String name) {
        locations.remove(name);
        return this;
    }

    public boolean upload() {
        return new WorldUploader(this).upload();
    }

    @Override
    public void changeName(String name) {
        File folder = bukkit().getWorldFolder();
        unload(true);

        if (folder.renameTo(new File(folder.getParent() + File.separator + name))) {
            WorldCreator wc = new WorldCreator(name)
                    .environment(World.Environment.valueOf(environment))
                    .type(WorldType.valueOf(worldType))
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
                BukkitCoreSystem.getInstance().getMessager().send(p, "§7§oDeine aktuelle Welt ist nicht mehr zugänglich. Du wurdest auf die Hauptwelt verschoben.");
            }
        }

        Bukkit.unloadWorld(bukkit(), save);
    }

    @Override
    public boolean delete() {
        unload(false);

        try {
            FileUtils.deleteDirectory(bukkit().getWorldFolder());
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
            if (!config.exists()) config.createNewFile();

            FileUtils.writeStringToFile(config, CoreSystem.getInstance().getGson().toJson(this, getClass()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setupWorld() {
        World w = bukkit();

        w.setDifficulty(Difficulty.valueOf(difficulty));
        w.setSpawnLocation(spawnLocation[0], spawnLocation[1], spawnLocation[2]);
        w.setPVP(pvp);
        w.setKeepSpawnInMemory(keepSpawnInMemory);

        if (!allowAnimals) {
            w.setAnimalSpawnLimit(0);
            w.setWaterAnimalSpawnLimit(0);
        }
        if (!allowMonsters) {
            w.setMonsterSpawnLimit(0);
        }
    }

    public void purgeAnimals() {
        for (Entity entity : bukkit().getEntities()) {
            if (entity instanceof Squid || entity instanceof Animals) {
                entity.remove();
            }
        }
    }

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
