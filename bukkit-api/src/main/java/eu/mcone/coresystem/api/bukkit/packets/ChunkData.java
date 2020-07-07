package eu.mcone.coresystem.api.bukkit.packets;

import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public abstract class ChunkData implements Serializable {

    private transient CodecRegistry codecRegistry;

    public ChunkData(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

}
