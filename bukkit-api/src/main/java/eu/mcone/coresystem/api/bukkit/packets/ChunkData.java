package eu.mcone.coresystem.api.bukkit.packets;

import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class ChunkData implements Serializable {

    public ChunkData() {
    }

    public abstract int getLength();
}
