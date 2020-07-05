package eu.mcone.coresystem.api.bukkit.packets;

import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Chunk implements Serializable {

    private transient final CodecRegistry codecRegistry;

    public Chunk(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }
}
