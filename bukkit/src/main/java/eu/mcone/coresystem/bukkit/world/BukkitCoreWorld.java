/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldProperties;
import eu.mcone.coresystem.core.annotation.DontObfuscate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

@AllArgsConstructor
@Getter
@DontObfuscate
public class BukkitCoreWorld implements CoreWorld {

    private String name;
    private String worldType, environment, difficulty, generator, generatorSettings;
    private boolean generateStructures;
    private WorldProperties properties;
    private int[] spawnLocation;

    private Map<String, CoreLocation> locations;

    @Override
    public World bukkit() {
        return Bukkit.getWorld(name);
    }

    @Override
    public CoreWorld setWorldType(WorldType worldType) {
        this.worldType = worldType.toString();
        return this;
    }

    @Override
    public CoreWorld setEnvironment(World.Environment environment) {
        this.environment = environment.toString();
        return this;
    }

    @Override
    public CoreWorld setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty.toString();
        return this;
    }

    @Override
    public CoreWorld setGenerator(String generator) {
        this.generator = generator;
        return this;
    }

    @Override
    public CoreWorld setGeneratorSettings(String settings) {
        this.generatorSettings = settings;
        return this;
    }

    @Override
    public CoreLocation getLocation(String name) {
        return locations.getOrDefault(name, null);
    }

    @Override
    public CoreWorld generateStructures(boolean generate) {
        this.generateStructures = generate;
        return this;
    }

    @Override
    public CoreWorld setSpawnLocation(Location loc) {
        this.spawnLocation = new int[] {(int) loc.getX(), (int) loc.getY(), (int) loc.getZ()};
        bukkit().setSpawnLocation(spawnLocation[0], spawnLocation[1], spawnLocation[2]);

        return this;
    }

    @Override
    public BukkitCoreWorld addLocation(String name, Location loc) {
        if (loc.getWorld().getName().equalsIgnoreCase(name)) {
            locations.put(name, new CoreLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
        }
        return this;
    }

    public boolean upload() {
        return new WorldUploader(this).upload();
    }

    @Override
    public BukkitCoreWorld changeName(String name) {
        File folder = bukkit().getWorldFolder();
        Bukkit.getServer().unloadWorld(this.name, true);

        if (folder.renameTo(new File(folder.getParent()+File.separator+name))) {
            WorldCreator wc = new WorldCreator(name)
                    .environment(World.Environment.valueOf(environment))
                    .type(WorldType.valueOf(worldType))
                    .generateStructures(generateStructures);

            if (generator != null) {
                wc.generator(generator);
                if (generatorSettings != null) wc.generatorSettings(generatorSettings);
            }

            ((WorldManager) CoreSystem.getInstance().getWorldManager()).setupWorld(wc.createWorld(), this);
        }

        return this;
    }

    @Override
    public boolean delete() {
        Bukkit.unloadWorld(bukkit(), false);
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
        ((WorldManager) CoreSystem.getInstance().getWorldManager()).setupWorld(bukkit(), this);
        File config = new File(bukkit().getWorldFolder(), WorldManager.CONFIG_NAME);

        try (Writer writer = new FileWriter(config)) {
            System.out.println("writing json to "+config.getPath());
            CoreSystem.getInstance().getGson().toJson(this, getClass(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return CoreSystem.getInstance().getGson().toJson(this, getClass());
    }

}
