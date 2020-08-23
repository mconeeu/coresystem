package eu.mcone.coresystem.api.bukkit.codec;

import lombok.Getter;

import java.util.List;

@Getter
public class MultipleCodecCallback extends CodecCallback {

    private final List<Codec<?, ?>> codecs;

    public MultipleCodecCallback(List<Codec<?, ?>> codecs, byte[] migratedCodecs, int migrated) {
        super(migratedCodecs, migrated);
        this.codecs = codecs;
    }
}
