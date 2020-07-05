package eu.mcone.coresystem.api.bukkit.packets;

import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class ChunkData {

    private CodecRegistry codecRegistry;

    public ChunkData(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    public abstract byte[] serialize();

    public abstract void deserialize();
}
