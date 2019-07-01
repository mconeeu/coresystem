/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public final class WorldCreateProperties {

    private long seed;
    private WorldType worldType;
    private World.Environment environment;
    private String generator, generatorSettings;
    @Builder.Default
    private boolean generateStructures = true, autoSave = true, pvp = true, allowAnimals = true, allowMonsters = true, spawnAnimals = false, spawnMonsters = false, keepSpawnInMemory = true;
    private Difficulty difficulty;

    public static WorldCreateProperties fromMap(Map<String, String> map) throws IllegalArgumentException {
        WorldCreateProperties builder = new WorldCreateProperties();
        System.out.println(map);

        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Field f = WorldCreateProperties.class.getDeclaredField(entry.getKey());

                if (f.getType().isEnum()) {
                    try {
                        setField(f, builder, f.getType().getMethod("valueOf", String.class).invoke(null, entry.getValue()));
                    } catch (InvocationTargetException e) {
                        throw new IllegalArgumentException("Illegal value for Enum "+f.getType().getSimpleName()+": "+entry.getValue(), e);
                    }
                } else {
                    if (f.getType().equals(String.class)) {
                        setField(f, builder, entry.getValue());
                    } else if (f.getType().equals(long.class)) {
                        try {
                            setField(f, builder, Long.parseLong(entry.getValue()));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Wrong type for field "+entry.getKey(), e);
                        }
                    } else if (f.getType().equals(boolean.class)) {
                        setField(f, builder, Boolean.parseBoolean(entry.getValue()));
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field!", e);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return builder;
    }

    private static void setField(Field f, Object object, Object value) throws IllegalAccessException {
        f.setAccessible(true);
        f.set(object, value);
        f.setAccessible(false);
    }

}
