package eu.mcone.coresystem.api.bukkit.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DeserializeCallback {

    private final List<Codec<?, ?>> codecs;
    private final byte[] migratedCodecs;
    private final int migrated;
}
