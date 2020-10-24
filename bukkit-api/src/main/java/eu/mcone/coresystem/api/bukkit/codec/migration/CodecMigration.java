package eu.mcone.coresystem.api.bukkit.codec.migration;

import eu.mcone.coresystem.api.bukkit.codec.Codec;

import java.io.DataInputStream;

public interface CodecMigration {

    void registerMigrationClass(Class<? extends MigrateCodec> clazz);

    void unRegisterMigrationClass(Class<? extends MigrateCodec> codec);

    boolean checkForMigration(byte version, Class<? extends Codec<?, ?>> codecClass);

    MigrationCallback migrate(Class<? extends Codec<?, ?>> codecClass, DataInputStream dataInputStream);

}
