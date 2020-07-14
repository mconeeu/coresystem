package eu.mcone.coresystem.api.bukkit.npc.capture;

import eu.mcone.coresystem.api.bukkit.codec.Codec;

import java.util.List;
import java.util.Map;

public interface MotionChunk {

    MotionChunkData getChunkData();

    interface MotionChunkData {
        Map<Integer, List<Codec<?, ?>>> getCodecs();
    }
}
