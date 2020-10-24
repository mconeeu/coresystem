package eu.mcone.coresystem.api.bukkit.codec.migration;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;

/**
 * NOTE: Each class must have a static variable named VERSION with the type byte containing the old version!
 */
@Getter
@NoArgsConstructor
public abstract class MigrateCodec {

    /**
     * migrates the old codec to the newer one
     *
     * @param dataInputStream {@link DataInputStream}
     * @return MigrationCallback
     */
    public abstract MigrationCallback migrate(DataInputStream dataInputStream);
}
