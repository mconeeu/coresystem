package eu.mcone.coresystem.api.bukkit.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CodecCallback {

    private final byte[] migratedCodecs;
    private final int migrated;
}
