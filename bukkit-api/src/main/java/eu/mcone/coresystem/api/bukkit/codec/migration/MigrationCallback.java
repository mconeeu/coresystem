package eu.mcone.coresystem.api.bukkit.codec.migration;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MigrationCallback {

    private final byte[] binary;
    private final Codec<?, ?> codec;
}
