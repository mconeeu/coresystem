package eu.mcone.coresystem.api.bukkit.codec;

import lombok.Getter;

@Getter
public class SingleCodecCallback extends CodecCallback {

    private final Codec<?, ?> codec;

    public SingleCodecCallback(Codec<?, ?> codec, byte[] migratedCodecs, int migrated) {
        super(migratedCodecs, migrated);
        this.codec = codec;
    }
}
