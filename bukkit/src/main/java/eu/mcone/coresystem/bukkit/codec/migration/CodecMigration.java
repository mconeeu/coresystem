package eu.mcone.coresystem.bukkit.codec.migration;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.migration.MigrateCodec;
import eu.mcone.coresystem.api.bukkit.codec.migration.MigrationCallback;

import java.io.DataInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CodecMigration implements eu.mcone.coresystem.api.bukkit.codec.migration.CodecMigration {

    private final HashMap<Byte, Class<? extends MigrateCodec>> migrations;

    public CodecMigration() {
        migrations = new HashMap<>();
    }

    public void registerMigrationClass(Class<? extends MigrateCodec> clazz) {
        try {
            byte version = clazz.getField("VERSION").getByte(null);
            if (!migrations.containsKey(version)) {
                migrations.put(version, clazz);
            } else {
                throw new IllegalStateException("A class with the version " + version + " registered");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void unRegisterMigrationClass(Class<? extends MigrateCodec> codec) {
        try {
            byte version = codec.getField("VERSION").getByte(null);
            migrations.remove(version);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean checkForMigration(byte version, Class<? extends Codec<?, ?>> codecClass) {
        try {
            return version < codecClass.getSuperclass().getField("BASE_VERSION").getByte(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return false;
    }

    public MigrationCallback migrate(Class<? extends Codec<?, ?>> codecClass, DataInputStream dataInputStream) {
        try {
            byte codecVersion = codecClass.getSuperclass().getField("BASE_VERSION").getByte(null);

            if (migrations.containsKey(codecVersion)) {
                MigrateCodec codec = migrations.get(codecVersion).getDeclaredConstructor().newInstance();
                return codec.migrate(dataInputStream);
            } else {
                throw new NullPointerException("Could not find a migration class for codec " + codecClass.getSimpleName() + " and version " + codecVersion);
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap<Byte, Class<? extends MigrateCodec>> getMigrationClasses() {
        return migrations;
    }
}
